import React, { useState, useEffect } from 'react';
import { ticketApi } from '../services/api';
import { Ticket } from '../types';
import TicketList from '../components/Tickets/TicketList';
import { UserCheck } from 'lucide-react';

const AssignedTickets: React.FC = () => {
  const [tickets, setTickets] = useState<Ticket[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadTickets();
  }, []);

  const loadTickets = async () => {
    try {
      const data = await ticketApi.getAssignedTickets();
      setTickets(data);
    } catch (error) {
      console.error('Error loading assigned tickets:', error);
    } finally {
      setLoading(false);
    }
  };

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
        <h1 className="text-2xl font-bold text-gray-900 flex items-center">
          <UserCheck className="h-6 w-6 mr-2" />
          Assigned Tickets ({tickets.length})
        </h1>
        <p className="mt-1 text-gray-600">
          Tickets assigned to you for resolution
        </p>
      </div>

      <TicketList tickets={tickets} showAssignee={false} />
    </div>
  );
};

export default AssignedTickets;