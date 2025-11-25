import request from '@/utils/request'

export const getLocationList = () => request.get('/locations/list')
export const deleteLocation = (locationsId) => request.delete(`/locations/delete/${locationsId}`)
export const addLocation = (locationForm) => request.post('/locations/add', locationForm)
export const updateLocation = (locationForm) => request.put('/locations/update', locationForm)