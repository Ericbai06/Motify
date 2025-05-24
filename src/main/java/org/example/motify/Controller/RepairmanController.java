package org.example.motify.Controller;

import org.example.motify.Entity.*;
import org.example.motify.Service.RepairmanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/repairmen")
public class RepairmanController {
    @Autowired
    private RepairmanService repairmanService;
} 