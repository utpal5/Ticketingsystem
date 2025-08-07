import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { ticketApi } from '../services/api';
import { useAuth } from '../contexts/AuthContext';
import { Ticket } from '../types';
import { 
  Ticket as TicketIcon, 
  Plus, 
  Clock, 
  CheckCircle, 
  AlertTriangle,
  TrendingUp
} from 'lucide-react';

const Dashboard: React.FC = () => {
  const { user } = useAuth();
  const [myTickets, setMyTickets] = useState<Ticket[]>([]);
  const [assignedTickets, setAssignedTickets] = useState<Ticket[]>([]);
  const [allTickets, setAllTickets] = useState<Ticket[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadDashboardData();
  }, [user]);

  const loadDashboardData = async () => {
    try {
      const myTicketsData = await ticketApi.getMyTickets();
      setMyTickets(myTicketsData);

      if (user?.role === 'SUPPORT_AGENT' || user?.role === 'ADMIN') {
        const assignedData = await ticketApi.getAssignedTickets();
        setAssignedTickets(assignedData);
      }

      if (user?.role === 'ADMIN') {
        const allData = await ticketApi.getAllTickets();
        setAllTickets(allData);
      }
    } catch (error) {
      console.error('Error loading dashboard data:', error);
    } finally {
      setLoading(false);
    }
  };

  const getTicketStats = (tickets: Ticket[]) => {
    return {
      open: tickets.filter(t => t.status === 'OPEN').length,
      inProgress: tickets.filter(t => t.status === 'IN_PROGRESS').length,
      resolved: tickets.filter(t => t.status === 'RESOLVED').length,
      closed: tickets.filter(t => t.status === 'CLOSED').length,
      urgent: tickets.filter(t => t.priority === 'URGENT').length,
    };
  };

  const myStats = getTicketStats(myTickets);
  const assignedStats = getTicketStats(assignedTickets);
  const allStats = getTicketStats(allTickets);

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">
          Welcome back, {user?.firstName}!
        </h1>
        <p className="mt-1 text-gray-600">
          Here's an overview of your support tickets and system activity.
        </p>
      </div>

      {/* Quick Actions */}
      <div className="bg-white p-6 rounded-lg shadow-sm">
        <h2 className="text-lg font-medium text-gray-900 mb-4">Quick Actions</h2>
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          <Link
            to="/create-ticket"
            className="flex items-center p-4 bg-blue-50 rounded-lg hover:bg-blue-100 transition-colors"
          >
            <Plus className="h-6 w-6 text-blue-600 mr-3" />
            <span className="text-sm font-medium text-blue-900">Create Ticket</span>
          </Link>
          <Link
            to="/my-tickets"
            className="flex items-center p-4 bg-green-50 rounded-lg hover:bg-green-100 transition-colors"
          >
            <TicketIcon className="h-6 w-6 text-green-600 mr-3" />
            <span className="text-sm font-medium text-green-900">My Tickets</span>
          </Link>
          {(user?.role === 'SUPPORT_AGENT' || user?.role === 'ADMIN') && (
            <Link
              to="/assigned-tickets"
              className="flex items-center p-4 bg-purple-50 rounded-lg hover:bg-purple-100 transition-colors"
            >
              <Clock className="h-6 w-6 text-purple-600 mr-3" />
              <span className="text-sm font-medium text-purple-900">Assigned Tickets</span>
            </Link>
          )}
          {user?.role === 'ADMIN' && (
            <Link
              to="/users"
              className="flex items-center p-4 bg-orange-50 rounded-lg hover:bg-orange-100 transition-colors"
            >
              <TrendingUp className="h-6 w-6 text-orange-600 mr-3" />
              <span className="text-sm font-medium text-orange-900">User Management</span>
            </Link>
          )}
        </div>
      </div>

      {/* My Tickets Stats */}
      <div className="bg-white p-6 rounded-lg shadow-sm">
        <h2 className="text-lg font-medium text-gray-900 mb-4">My Tickets Overview</h2>
        <div className="grid grid-cols-2 md:grid-cols-5 gap-4">
          <div className="text-center">
            <div className="text-2xl font-bold text-gray-900">{myStats.open}</div>
            <div className="text-sm text-gray-500">Open</div>
          </div>
          <div className="text-center">
            <div className="text-2xl font-bold text-blue-600">{myStats.inProgress}</div>
            <div className="text-sm text-gray-500">In Progress</div>
          </div>
          <div className="text-center">
            <div className="text-2xl font-bold text-green-600">{myStats.resolved}</div>
            <div className="text-sm text-gray-500">Resolved</div>
          </div>
          <div className="text-center">
            <div className="text-2xl font-bold text-gray-600">{myStats.closed}</div>
            <div className="text-sm text-gray-500">Closed</div>
          </div>
          <div className="text-center">
            <div className="text-2xl font-bold text-red-600">{myStats.urgent}</div>
            <div className="text-sm text-gray-500">Urgent</div>
          </div>
        </div>
      </div>

      {/* Assigned Tickets Stats (Support Agents and Admins) */}
      {(user?.role === 'SUPPORT_AGENT' || user?.role === 'ADMIN') && (
        <div className="bg-white p-6 rounded-lg shadow-sm">
          <h2 className="text-lg font-medium text-gray-900 mb-4">Assigned Tickets Overview</h2>
          <div className="grid grid-cols-2 md:grid-cols-5 gap-4">
            <div className="text-center">
              <div className="text-2xl font-bold text-gray-900">{assignedStats.open}</div>
              <div className="text-sm text-gray-500">Open</div>
            </div>
            <div className="text-center">
              <div className="text-2xl font-bold text-blue-600">{assignedStats.inProgress}</div>
              <div className="text-sm text-gray-500">In Progress</div>
            </div>
            <div className="text-center">
              <div className="text-2xl font-bold text-green-600">{assignedStats.resolved}</div>
              <div className="text-sm text-gray-500">Resolved</div>
            </div>
            <div className="text-center">
              <div className="text-2xl font-bold text-gray-600">{assignedStats.closed}</div>
              <div className="text-sm text-gray-500">Closed</div>
            </div>
            <div className="text-center">
              <div className="text-2xl font-bold text-red-600">{assignedStats.urgent}</div>
              <div className="text-sm text-gray-500">Urgent</div>
            </div>
          </div>
        </div>
      )}

      {/* System Overview (Admin Only) */}
      {user?.role === 'ADMIN' && (
        <div className="bg-white p-6 rounded-lg shadow-sm">
          <h2 className="text-lg font-medium text-gray-900 mb-4">System Overview</h2>
          <div className="grid grid-cols-2 md:grid-cols-5 gap-4">
            <div className="text-center">
              <div className="text-2xl font-bold text-gray-900">{allStats.open}</div>
              <div className="text-sm text-gray-500">Total Open</div>
            </div>
            <div className="text-center">
              <div className="text-2xl font-bold text-blue-600">{allStats.inProgress}</div>
              <div className="text-sm text-gray-500">Total In Progress</div>
            </div>
            <div className="text-center">
              <div className="text-2xl font-bold text-green-600">{allStats.resolved}</div>
              <div className="text-sm text-gray-500">Total Resolved</div>
            </div>
            <div className="text-center">
              <div className="text-2xl font-bold text-gray-600">{allStats.closed}</div>
              <div className="text-sm text-gray-500">Total Closed</div>
            </div>
            <div className="text-center">
              <div className="text-2xl font-bold text-red-600">{allStats.urgent}</div>
              <div className="text-sm text-gray-500">Total Urgent</div>
            </div>
          </div>
        </div>
      )}

      {/* Recent Tickets */}
      <div className="bg-white p-6 rounded-lg shadow-sm">
        <div className="flex justify-between items-center mb-4">
          <h2 className="text-lg font-medium text-gray-900">Recent Tickets</h2>
          <Link to="/my-tickets" className="text-blue-600 hover:text-blue-800 text-sm">
            View all →
          </Link>
        </div>
        <div className="space-y-3">
          {myTickets.slice(0, 5).map((ticket) => (
            <Link
              key={ticket.id}
              to={`/tickets/${ticket.id}`}
              className="block p-3 rounded-lg border border-gray-200 hover:bg-gray-50 transition-colors"
            >
              <div className="flex justify-between items-start">
                <div>
                  <h3 className="text-sm font-medium text-gray-900">{ticket.subject}</h3>
                  <p className="text-xs text-gray-500 mt-1">
                    {ticket.status} • {ticket.priority} Priority
                  </p>
                </div>
                <span className="text-xs text-gray-400">
                  #{ticket.id}
                </span>
              </div>
            </Link>
          ))}
          {myTickets.length === 0 && (
            <div className="text-center py-8 text-gray-500">
              No tickets found. Create your first ticket to get started!
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default Dashboard;