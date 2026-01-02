import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/admins',
    component: () => import('@/views/admin/layout/Layout.vue'),
    redirect: 'admins/dashboard',
    children: [
      {
        path: '/admins/dashboard',
        name: 'Dashboard',
        component: () => import('@/views/admin/dashboard/Dashboard.vue')
      }
    ]
  },
  {
    path: '/users',
    component: () => import('@/views/users/layout/Layout.vue'),
    redirect: '/users/dashboard',
    children: [
      {
        path: '/users/dashboard',
        name: 'UsersDashboard',
        component: () => import('@/views/users/dashboard/Dashboard.vue')
      },
      {
        path: '/users/movies',
        name: 'UsersMovies',
        component: () => import('@/views/users/movie/Movies.vue')
      },
      {
        path: '/users/user',
        name: 'UsersUser',
        component: () => import('@/views/users/user/User.vue')
      },
      {
        path: '/users/movieInfo/:movieId',
        name: 'MovieInfo',
        component: () => import('@/views/users/movie/MovieInfo.vue')
      },
      {
        path: '/users/cinemas',
        name: 'Cinemas',
        component: () => import('@/views/users/cinema/Cinemas.vue')
      },
      {
        path: '/users/ranking',
        name: 'Ranking',
        component: () => import('@/views/users/rank/Ranking.vue')
      },
      {
        path: '/users/showtimes/:cinemaId',
        name: 'UserShowtimes',
        component: () => import('@/views/users/showtime/Showtime.vue')
      },
      {
        path: '/users/seats',
        name: 'Seats',
        component: () => import('@/views/users/seat/Seats.vue')
      },
      {
        path: '/users/orders',
        name: 'Order',
        component: () => import('@/views/users/order/Order.vue')
      },
      {
        path: '/users/searchs',
        name: 'Searchs',
        component: () => import('@/views/users/search/Search.vue')
      }
    ]
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/login/Login.vue')
  },
  {
    path: '/register',
    name: 'register',
    component: () => import('@/views/register/Register.vue')

  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
