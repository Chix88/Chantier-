package com.example.data.model

data class AllocationDetail(
    val allocationId: Long,
    val date: String,
    val chefId: Long,
    val chefName: String,
    val chefSpecialty: String,
    val chefTotalWorkers: Int,
    val blocId: Long,
    val blocName: String,
    val blocCode: String,
    val blocColorHex: String,
    val taskId: Long,
    val taskTitle: String,
    val taskCategory: String,
    val taskStatus: String,
    val taskPriority: String,
    val workersCount: Int,
    val note: String,
    val customRendement: Double = 0.0,
    val isSecondaryTask: Boolean = false,
    val linkedTaskId: Long? = null,
    val linkedTaskTitle: String? = null
)

data class TaskWithAllocationInfo(
    val task: TaskItem,
    val bloc: Bloc,
    val totalWorkersAssigned: Int,
    val allocations: List<AllocationDetail>
)

data class ChefWithAllocations(
    val chef: TeamLeader,
    val allocatedWorkers: Int,
    val remainingWorkers: Int,
    val allocations: List<AllocationDetail>
)

data class TaskWorkerSummary(
    val taskCategory: String,
    val totalWorkers: Int,
    val taskCount: Int
)

data class BlocWorkerSummary(
    val blocId: Long,
    val blocName: String,
    val blocColorHex: String,
    val totalWorkers: Int,
    val activeTasksCount: Int,
    val chefsAssigned: List<String>
)
