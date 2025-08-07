import axios from 'axios';
import { User, Ticket, Comment, TicketFormData, UserFormData } from '../types';

// Configure base URL - adjust this to match your Spring Boot backend
const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add auth token
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('authToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Response interceptor to handle auth errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('authToken');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export const authApi = {
  login: async (credentials: { username: string; password: string }) => {
    const response = await api.post('/auth/login', credentials);
    return response.data;
  },
  
  getCurrentUser: async (): Promise<User> => {
    const response = await api.get('/auth/me');
    return response.data;
  },
};

export const ticketApi = {
  getMyTickets: async (): Promise<Ticket[]> => {
    const response = await api.get('/tickets/my');
    return response.data;
  },

  getAllTickets: async (): Promise<Ticket[]> => {
    const response = await api.get('/tickets');
    return response.data;
  },

  getAssignedTickets: async (): Promise<Ticket[]> => {
    const response = await api.get('/tickets/assigned');
    return response.data;
  },

  getTicketById: async (id: number): Promise<Ticket> => {
    const response = await api.get(`/tickets/${id}`);
    return response.data;
  },

  createTicket: async (ticketData: TicketFormData): Promise<Ticket> => {
    const response = await api.post('/tickets', ticketData);
    return response.data;
  },

  updateTicketStatus: async (id: number, status: string): Promise<Ticket> => {
    const response = await api.patch(`/tickets/${id}/status`, { status });
    return response.data;
  },

  assignTicket: async (id: number, assigneeId: number): Promise<Ticket> => {
    const response = await api.patch(`/tickets/${id}/assign`, { assigneeId });
    return response.data;
  },

  getTicketComments: async (ticketId: number): Promise<Comment[]> => {
    const response = await api.get(`/tickets/${ticketId}/comments`);
    return response.data;
  },

  addComment: async (ticketId: number, content: string): Promise<Comment> => {
    const response = await api.post(`/tickets/${ticketId}/comments`, { content });
    return response.data;
  },
};

export const userApi = {
  getAllUsers: async (): Promise<User[]> => {
    const response = await api.get('/users');
    return response.data;
  },

  createUser: async (userData: UserFormData): Promise<User> => {
    const response = await api.post('/users', userData);
    return response.data;
  },

  updateUserRole: async (id: number, role: string): Promise<User> => {
    const response = await api.patch(`/users/${id}/role`, { role });
    return response.data;
  },

  deleteUser: async (id: number): Promise<void> => {
    await api.delete(`/users/${id}`);
  },

  getSupportAgents: async (): Promise<User[]> => {
    const response = await api.get('/users/support-agents');
    return response.data;
  },
};

export default api;