import request from "@/utils/request"

export const generateOrders = (showtimesIds) => request.post('/user/orders/buy', showtimesIds)

export const ordersList = () => request.get('/user/orders/list')

export const returnTickets = (orderId) => request.post(`/user/orders/return/${orderId}`)