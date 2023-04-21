package com.chalova.irina.todoapp.tasks.utils

sealed class TaskOrder(val orderType: OrderType) {

    class Priority(orderType: OrderType) : TaskOrder(orderType)
    class Date(orderType: OrderType) : TaskOrder(orderType)

    companion object {
        @JvmStatic
        fun getTaskOrderByName(orderName: String, orderTypeName: String): TaskOrder {
            val orderType = (OrderType.getOrderTypeByName(orderTypeName))
            return when (Orders.valueOf(orderName)) {
                Orders.Priority -> Priority(orderType)
                Orders.Date -> Date(orderType)
            }
        }
    }

    enum class OrderType {
        Ascending,
        Descending;

        companion object {
            @JvmStatic
            fun getOrderTypeByName(type: String): OrderType {
                val orderType = values().find { it.name == type }
                return orderType ?: throw IllegalArgumentException("no such order type")
            }
        }
    }

    enum class Orders {
        Priority,
        Date;

        companion object {

            @JvmStatic
            fun getOrderByTaskOrder(taskOrder: TaskOrder): Orders {
                return when (taskOrder) {
                    is TaskOrder.Date -> Date
                    is TaskOrder.Priority -> Priority
                }
            }
        }
    }
}
