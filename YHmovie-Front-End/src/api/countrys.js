import request from '@/utils/request'

export const getCountryList = () => request.get('/countrys/list')

export const deleteCountry = (countrysId) => request.delete(`/countrys/delete/${countrysId}`)

export const addCountry = (countryName) => request.post(`/countrys/add?countryName=${countryName}`)

export const updateCountry = (countryForm) => request.put('/countrys/update', countryForm)