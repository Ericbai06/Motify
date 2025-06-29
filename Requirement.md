课程实践项⽬——⻋辆维修管理系统

⼀、概述

⻋辆维修管理系统是⼀个专为⻋辆维修业务设计的综合性数据库应⽤系统，

旨在提⾼维修业务的管理效率、降低运营成本，并为客⼾提供更优质的服

务。该系统涵盖了⻋辆信息管理、客⼾信息管理、维修⼯单管理、库存管

理、财务管理等多个核⼼模块，通过整合各个业务环节的数据，实现信息的

实时共享和⾼效流转。

⼆、系统功能需求

1. ⽤⼾功能

⽤⼾可以注册和登录系统；

⽤⼾可以提交⻋辆报修信息；

⽤⼾可以查询⾃⼰的账⼾信息、⻋辆信息、维修记录和维修⼯单信息；

⽤⼾可以对维修进度和维修结果进⾏反馈，如催单、服务评分等；

⽤⼾可以查看⾃⼰的历史维修记录。

2. 维修⼈员功能

维修⼈员可以登录系统；

维修⼈员可以查看⾃⼰的账⼾信息，包括⼯种（如漆⼯、焊⼯、机修

等，仅限⼀种）和时薪；

维修⼈员可接收或拒绝系统分配的维修⼯单（拒绝后系统将⾃动重新分

配）；

维修⼈员需记录维修过程中消耗的材料及价格；

维修⼈员可更新维修进度和维修结果；

维修⼈员可以查询⾃⼰的历史维修记录⼯时费收⼊。

3. 系统管理员功能

管理系统中⽤⼾信息、维修⼈员信息、⻋辆信息和维修⼯单；

维护系统中的数据⼀致性与完整性；监控各类数据的运⾏状况与统计结果。

三、系统⼯作流程概述

1. ⽤⼾注册登录后，可提交维修申请；
2. 系统⽣成维修⼯单，并根据需要⾃动分配给⼀名或多名维修⼈员；
3. 维修⼈员接单后，记录维修材料消耗与进度，提交维修结果；
4. ⼯单完成后，系统⾃动计算费⽤（⼯时费 + 材料费）；
5. ⽤⼾可查询维修记录并提交服务反馈；
6. 系统管理员监控和维护整个流程的正常运⾏。

四、查询与数据分析需求

1. 基础查询需求（按⻆⾊）

⽤⼾查询需求

查询账⼾信息、⻋辆信息；

查询维修⼯单信息；

查询历史维修记录。

维修⼈员查询需求

查询账⼾信息（含⼯种、时薪）；

查询当前和历史维修⼯单；

查询累计⼯时费收⼊。

系统管理员查询需求

查询所有⽤⼾、维修⼈员、⻋辆和维修⼯单信息；

查询所有历史维修记录与⼯时费发放记录。

2. 进阶统计与分析需求（扩展）

数据统计类查询（涉及多表连接/分组统计）

统计各⻋型的维修次数与平均维修费⽤⻋型维修统计：统计所有⻋型的维修频率；统计特定⻋型最常出现的故

障类型

成本分析：按季度或⽉份统计维修费⽤构成（⼯时费、材料费⽐例等）

筛选负⾯反馈⼯单及涉及的员⼯

统计⼀段时间内，不同⼯种接的/完成的任务数量以及在总任务数量中的

占⽐，辅助公司进⾏针对不同⼯种的招聘

统计到⽬前为⽌尚未完成的维修任务/订单的数量、类型，按照涉及⼯

种、⼯⼈、⻋辆等分别查询

数据维护联动需求（可选触发器/程序控制）

当某⼯单状态改为“已完成”时，⾃动汇总该⼯单的⼯时费与材料

⽤⼾或维修⼈员信息修改后，相应信息在历史记录表中⾃动同步更新

（如姓名、联系⽅式变更）

⼯单删除时，⾃动级联删除材料记录、⼯单-维修⼈员关联记录等

需要合理设置外键的 ON DELETE CASCADE 或触发器维护

事务控制

⼀次事务实现提交多个⼯单与维修⼈员分配，并确保数据⼀致性

如前台系统⼀次性上传多个报修请求，后台应通过事务控制，确保⼯单

⽣成与⼈员分配同时成功或同时回滚

在每⽉结算⼯时费时，批量统计所有维修⼈员的本⽉总⼯时与收⼊，并

写⼊表中

数据回滚

有数据的变动要有据可查，以便应对可能出现质量问题等特殊情况时需

要回溯

其他数据分析需求：

可结合业务逻辑⾃⾏挖掘需求并进⾏设计。

五、系统展⽰与实现说明

使⽤合理的界⾯进⾏功能展⽰（不限于Web，可使⽤桌⾯界⾯、命令⾏

交互等）

禁⽌直接通过数据库输⼊ SQL 语句来模拟系统功能可使⽤任意⽀持 SQL 的数据库平台（如 MySQL、Oracle、SQL Server

等）

系统需提供模拟测试数据，确保各模块功能逻辑正确可运⾏

六、提交内容

项⽬最终需提交以下内容：

设计的 ER 图；

数据库表结构说明（含索引说明）；

核⼼功能 SQL 语句说明；

存储过程和触发器说明（如有）；

系统源代码；

可执⾏程序包及运⾏说明⽂档。
