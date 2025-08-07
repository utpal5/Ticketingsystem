import React, { useState, useEffect } from 'react';
import { ticketApi } from '../services/api';
import { Ticket } from '../types';
import TicketList from '../components/Tickets/TicketList';
import { ClipboardList } from 'lucide-react';

const AllTickets: React.FC = () => {
  const [tickets, setTickets] = useState<Ticket[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadTickets();
  }, []);

  const loadTickets = async () => {
    try {
      const data = await ticketApi.getAllTickets();
      setTickets(data);
    } catch (error) {
      console.error('Error loading all tickets:', error);
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
          <ClipboardList className="h-6 w-6 mr-2" />
          All Tickets ({tickets.length})
        </h1>
        <p className="mt-1 text-gray-600">
          Complete overview of all support tickets in the system
        </p>
      </div>

      <TicketList tickets={tickets} showAssignee={true} />
    </div>
  );
};

export default AllTickets;