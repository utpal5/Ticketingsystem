import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ticketApi, userApi } from '../../services/api';
import { Ticket, Comment, User } from '../../types';
import { useAuth } from '../../contexts/AuthContext';
import { formatDistanceToNow } from 'date-fns';
import { 
  ArrowLeft, 
  User as UserIcon, 
  Clock, 
  AlertTriangle, 
  MessageCircle,
  Send
} from 'lucide-react';

const TicketDetail: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { user } = useAuth();
  const [ticket, setTicket] = useState<Ticket | null>(null);
  const [comments, setComments] = useState<Comment[]>([]);
  const [supportAgents, setSupportAgents] = useState<User[]>([]);
  const [loading, setLoading] = useState(true);
  const [commentText, setCommentText] = useState('');
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (id) {
      loadTicketData();
      if (user?.role === 'ADMIN' || user?.role === 'SUPPORT_AGENT') {
        loadSupportAgents();
      }
    }
  }, [id, user]);

  const loadTicketData = async () => {
    try {
      const [ticketData, commentsData] = await Promise.all([
        ticketApi.getTicketById(Number(id)),
        ticketApi.getTicketComments(Number(id))
      ]);
      setTicket(ticketData);
      setComments(commentsData);
    } catch (error) {
      console.error('Error loading ticket:', error);
    } finally {
      setLoading(false);
    }
  };

  const loadSupportAgents = async () => {
    try {
      const agents = await userApi.getSupportAgents();
      setSupportAgents(agents);
    } catch (error) {
      console.error('Error loading support agents:', error);
    }
  };

  const handleStatusChange = async (newStatus: string) => {
    if (ticket) {
      try {
        const updatedTicket = await ticketApi.updateTicketStatus(ticket.id, newStatus);
        setTicket(updatedTicket);
      } catch (error) {
        console.error('Error updating status:', error);
      }
    }
  };

  const handleAssignTicket = async (assigneeId: number) => {
    if (ticket) {
      try {
        const updatedTicket = await ticketApi.assignTicket(ticket.id, assigneeId);
        setTicket(updatedTicket);
      } catch (error) {
        console.error('Error assigning ticket:', error);
      }
    }
  };

  const handleAddComment = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!commentText.trim() || !ticket) return;

    setSubmitting(true);
    try {
      const newComment = await ticketApi.addComment(ticket.id, commentText);
      setComments([...comments, newComment]);
      setCommentText('');
    } catch (error) {
      console.error('Error adding comment:', error);
    } finally {
      setSubmitting(false);
    }
  };

  const canModifyTicket = () => {
    if (!user || !ticket) return false;
    return user.role === 'ADMIN' || 
           (user.role === 'SUPPORT_AGENT' && ticket.assignedTo?.id === user.id) ||
           ticket.createdBy.id === user.id;
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'OPEN': return 'bg-gray-100 text-gray-800';
      case 'IN_PROGRESS': return 'bg-blue-100 text-blue-800';
      case 'RESOLVED': return 'bg-green-100 text-green-800';
      case 'CLOSED': return 'bg-gray-100 text-gray-600';
      default: return 'bg-gray-100 text-gray-800';
    }
  };

  const getPriorityColor = (priority: string) => {
    switch (priority) {
      case 'LOW': return 'text-green-600';
      case 'MEDIUM': return 'text-yellow-600';
      case 'HIGH': return 'text-orange-600';
      case 'URGENT': return 'text-red-600';
      default: return 'text-gray-600';
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  if (!ticket) {
    return (
      <div className="text-center py-12">
        <h3 className="text-lg font-medium text-gray-900">Ticket not found</h3>
        <button
          onClick={() => navigate('/my-tickets')}
          className="mt-4 text-blue-600 hover:text-blue-800"
        >
          Go back to tickets
        </button>
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      <div className="flex items-center justify-between">
        <button
          onClick={() => navigate(-1)}
          className="flex items-center text-gray-600 hover:text-gray-900 transition-colors"
        >
          <ArrowLeft className="h-4 w-4 mr-1" />
          Back
        </button>
        
        <div className="text-sm text-gray-500">
          Ticket #{ticket.id}
        </div>
      </div>

      <div className="bg-white shadow-sm rounded-lg overflow-hidden">
        <div className="px-6 py-4 border-b border-gray-200">
          <div className="flex items-start justify-between">
            <div>
              <h1 className="text-xl font-semibold text-gray-900">{ticket.subject}</h1>
              <div className="mt-2 flex items-center space-x-4">
                <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${getStatusColor(ticket.status)}`}>
                  {ticket.status.replace('_', ' ')}
                </span>
                <div className={`flex items-center ${getPriorityColor(ticket.priority)}`}>
                  <AlertTriangle className="h-4 w-4 mr-1" />
                  <span className="text-sm font-medium">{ticket.priority} Priority</span>
                </div>
              </div>
            </div>
            
            {(user?.role === 'ADMIN' || user?.role === 'SUPPORT_AGENT') && (
              <div className="flex space-x-2">
                <select
                  value={ticket.status}
                  onChange={(e) => handleStatusChange(e.target.value)}
                  className="text-sm border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                >
                  <option value="OPEN">Open</option>
                  <option value="IN_PROGRESS">In Progress</option>
                  <option value="RESOLVED">Resolved</option>
                  <option value="CLOSED">Closed</option>
                </select>
                
                {user?.role === 'ADMIN' && (
                  <select
                    value={ticket.assignedTo?.id || ''}
                    onChange={(e) => e.target.value && handleAssignTicket(Number(e.target.value))}
                    className="text-sm border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
                  >
                    <option value="">Unassigned</option>
                    {supportAgents.map((agent) => (
                      <option key={agent.id} value={agent.id}>
                        {agent.firstName} {agent.lastName}
                      </option>
                    ))}
                  </select>
                )}
              </div>
            )}
          </div>
        </div>

        <div className="px-6 py-4">
          <div className="prose max-w-none">
            <p className="text-gray-700 whitespace-pre-wrap">{ticket.description}</p>
          </div>
          
          <div className="mt-6 grid grid-cols-1 md:grid-cols-2 gap-4 text-sm">
            <div>
              <div className="flex items-center text-gray-500">
                <UserIcon className="h-4 w-4 mr-2" />
                <span className="font-medium">Created by:</span>
                <span className="ml-1">{ticket.createdBy.firstName} {ticket.createdBy.lastName}</span>
              </div>
              <div className="flex items-center text-gray-500 mt-2">
                <Clock className="h-4 w-4 mr-2" />
                <span className="font-medium">Created:</span>
                <span className="ml-1">{formatDistanceToNow(new Date(ticket.createdAt), { addSuffix: true })}</span>
              </div>
            </div>
            <div>
              {ticket.assignedTo && (
                <div className="flex items-center text-gray-500">
                  <UserIcon className="h-4 w-4 mr-2" />
                  <span className="font-medium">Assigned to:</span>
                  <span className="ml-1">{ticket.assignedTo.firstName} {ticket.assignedTo.lastName}</span>
                </div>
              )}
              <div className="flex items-center text-gray-500 mt-2">
                <Clock className="h-4 w-4 mr-2" />
                <span className="font-medium">Last updated:</span>
                <span className="ml-1">{formatDistanceToNow(new Date(ticket.updatedAt), { addSuffix: true })}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div className="bg-white shadow-sm rounded-lg">
        <div className="px-6 py-4 border-b border-gray-200">
          <h2 className="text-lg font-medium text-gray-900 flex items-center">
            <MessageCircle className="h-5 w-5 mr-2" />
            Comments ({comments.length})
          </h2>
        </div>
        
        <div className="divide-y divide-gray-200">
          {comments.map((comment) => (
            <div key={comment.id} className="px-6 py-4">
              <div className="flex items-start space-x-3">
                <div className="flex-shrink-0">
                  <div className="h-8 w-8 bg-gray-300 rounded-full flex items-center justify-center">
                    <UserIcon className="h-4 w-4 text-gray-600" />
                  </div>
                </div>
                <div className="flex-1">
                  <div className="flex items-center space-x-2">
                    <span className="text-sm font-medium text-gray-900">
                      {comment.author.firstName} {comment.author.lastName}
                    </span>
                    <span className="text-xs text-gray-500">
                      {formatDistanceToNow(new Date(comment.createdAt), { addSuffix: true })}
                    </span>
                  </div>
                  <p className="mt-1 text-sm text-gray-700 whitespace-pre-wrap">
                    {comment.content}
                  </p>
                </div>
              </div>
            </div>
          ))}
          
          {comments.length === 0 && (
            <div className="px-6 py-8 text-center text-gray-500">
              No comments yet. Be the first to add one!
            </div>
          )}
        </div>

        {canModifyTicket() && (
          <div className="px-6 py-4 border-t border-gray-200 bg-gray-50">
            <form onSubmit={handleAddComment} className="flex space-x-3">
              <div className="flex-1">
                <textarea
                  rows={3}
                  placeholder="Add a comment..."
                  value={commentText}
                  onChange={(e) => setCommentText(e.target.value)}
                  className="block w-full border-gray-300 rounded-md shadow-sm focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                />
              </div>
              <button
                type="submit"
                disabled={submitting || !commentText.trim()}
                className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
              >
                <Send className="h-4 w-4" />
              </button>
            </form>
          </div>
        )}
      </div>
    </div>
  );
};

export default TicketDetail;