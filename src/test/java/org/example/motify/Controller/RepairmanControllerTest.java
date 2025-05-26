package org.example.motify.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.motify.Entity.*;
import org.example.motify.Enum.MaintenanceStatus;
import org.example.motify.Service.RepairmanService;
import org.example.motify.Exception.ResourceNotFoundException;
import org.example.motify.Exception.BadRequestException;
import org.example.motify.Exception.AuthenticationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class RepairmanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RepairmanService repairmanService;

    @Autowired
    private ObjectMapper objectMapper;

    private Repairman testRepairman;
    private MaintenanceItem testMaintenanceItem;

    @BeforeEach
    void setUp() {
        testRepairman = new Repairman();
        testRepairman.setRepairmanId(1L);
        testRepairman.setUsername("testRepairman");
        testRepairman.setPassword("password");
        testRepairman.setName("Test Repairman");
        testRepairman.setPhone("1234567890");
        testRepairman.setSpecialty("Engine");
        testRepairman.setType("MECHANIC");

        testMaintenanceItem = new MaintenanceItem();
        testMaintenanceItem.setItemId(1L);
        testMaintenanceItem.setName("Test Item");
        testMaintenanceItem.setDescription("Test Description");
        testMaintenanceItem.setStatus(MaintenanceStatus.PENDING);
        testMaintenanceItem.setProgress(0);
        testMaintenanceItem.setResult("Pending");
        testMaintenanceItem.setCost(100.0);
    }

    @Test
    void register_Success() throws Exception {
        when(repairmanService.register(any(Repairman.class))).thenReturn(testRepairman);

        mockMvc.perform(post("/api/repairmen/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRepairman)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(testRepairman.getUsername()));
    }

    @Test
    void register_BadRequest() throws Exception {
        when(repairmanService.register(any(Repairman.class)))
                .thenThrow(new BadRequestException("Username already exists"));

        mockMvc.perform(post("/api/repairmen/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRepairman)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_Success() throws Exception {
        when(repairmanService.login(anyString(), anyString())).thenReturn(Optional.of(testRepairman));

        mockMvc.perform(post("/api/repairmen/login")
                .param("username", "testRepairman")
                .param("password", "password"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(testRepairman.getUsername()));
    }

    @Test
    void login_AuthenticationFailed() throws Exception {
        when(repairmanService.login(anyString(), anyString()))
                .thenThrow(new AuthenticationException("Invalid credentials"));

        mockMvc.perform(post("/api/repairmen/login")
                .param("username", "testRepairman")
                .param("password", "wrongPassword"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRepairmanInfo_Success() throws Exception {
        when(repairmanService.getRepairmanById(anyLong())).thenReturn(Optional.of(testRepairman));

        mockMvc.perform(get("/api/repairmen/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.repairmanId").value(testRepairman.getRepairmanId()));
    }

    @Test
    void getRepairmanInfo_NotFound() throws Exception {
        when(repairmanService.getRepairmanById(anyLong()))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/repairmen/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateMaintenanceItem_Success() throws Exception {
        when(repairmanService.updateMaintenanceItem(anyLong(), any(MaintenanceItem.class)))
                .thenReturn(testMaintenanceItem);

        mockMvc.perform(put("/api/repairmen/maintenance-items/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testMaintenanceItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemId").value(testMaintenanceItem.getItemId()));
    }

    @Test
    void getCurrentRecords_Success() throws Exception {
        when(repairmanService.getRepairmanCurrentRecords(anyLong()))
                .thenReturn(Arrays.asList(testMaintenanceItem));

        mockMvc.perform(get("/api/repairmen/1/current-records"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].itemId").value(testMaintenanceItem.getItemId()));
    }

    @Test
    void getTotalIncome_Success() throws Exception {
        when(repairmanService.calculateTotalIncome(anyLong())).thenReturn(1000.0);

        mockMvc.perform(get("/api/repairmen/1/total-income"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(1000.0));
    }

    @Test
    void acceptMaintenanceItem_Success() throws Exception {
        when(repairmanService.acceptMaintenanceItem(anyLong(), anyLong()))
                .thenReturn(testMaintenanceItem);

        mockMvc.perform(post("/api/repairmen/1/accept/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.itemId").value(testMaintenanceItem.getItemId()));
    }
} 