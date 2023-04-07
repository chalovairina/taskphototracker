package com.chalova.irina.todoapp.tasks.utils

sealed class TaskOrder(val orderTitle: Orders, val orderType: OrderType) {

    class Priority(orderType: OrderType): TaskOrder(Orders.Priority, orderType)
    class Date(orderType: OrderType): TaskOrder(Orders.Date, orderType)

    sealed class OrderType(val type: OrderTypes) {
        object Ascending: OrderType(OrderTypes.Ascending)
        object Descending: OrderType(OrderTypes.Descending)

        companion object {
            fun getOrderTypeByName(name: String): OrderType {
                return when (OrderTypes.valueOf(name)) {
                    OrderTypes.Ascending -> Ascending
                    OrderTypes.Descending -> Descending
                }
            }
        }
    }

    companion object {
        fun getTaskOrderByName(orderName: String, orderTypeName: String): TaskOrder {
            val orderType = (OrderType.getOrderTypeByName(orderTypeName))
            return when (Orders.valueOf(orderName)) {
                Orders.Priority -> Priority(orderType)
                Orders.Date -> Date(orderType)
            }
        }
    }

    enum class OrderTypes {
        Ascending,
        Descending
    }

    enum class Orders {
        Priority,
        Date
    }
}
