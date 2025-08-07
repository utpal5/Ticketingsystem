import React from 'react';
import { NavLink } from 'react-router-dom';
import { 
  Home, 
  Ticket, 
  Plus, 
  Users, 
  Settings, 
  UserCheck,
  ClipboardList 
} from 'lucide-react';
import { useAuth } from '../../contexts/AuthContext';

const Sidebar: React.FC = () => {
  const { user } = useAuth();

  const navItems = [
    {
      name: 'Dashboard',
      href: '/dashboard',
      icon: Home,
      roles: ['USER', 'SUPPORT_AGENT', 'ADMIN']
    },
    {
      name: 'My Tickets',
      href: '/my-tickets',
      icon: Ticket,
      roles: ['USER', 'SUPPORT_AGENT', 'ADMIN']
    },
    {
      name: 'Create Ticket',
      href: '/create-ticket',
      icon: Plus,
      roles: ['USER', 'SUPPORT_AGENT', 'ADMIN']
    },
    {
      name: 'Assigned Tickets',
      href: '/assigned-tickets',
      icon: UserCheck,
      roles: ['SUPPORT_AGENT', 'ADMIN']
    },
    {
      name: 'All Tickets',
      href: '/all-tickets',
      icon: ClipboardList,
      roles: ['ADMIN']
    },
    {
      name: 'User Management',
      href: '/users',
      icon: Users,
      roles: ['ADMIN']
    }
  ];

  const filteredNavItems = navItems.filter(item => 
    item.roles.includes(user?.role || 'USER')
  );

  return (
    <div className="bg-gray-900 text-white w-64 min-h-screen p-4">
      <nav className="space-y-2">
        {filteredNavItems.map((item) => (
          <NavLink
            key={item.name}
            to={item.href}
            className={({ isActive }) =>
              `flex items-center px-4 py-2 text-sm font-medium rounded-md transition-colors ${
                isActive
                  ? 'bg-blue-600 text-white'
                  : 'text-gray-300 hover:bg-gray-700 hover:text-white'
              }`
            }
          >
            <item.icon className="mr-3 h-5 w-5" />
            {item.name}
          </NavLink>
        ))}
      </nav>
    </div>
  );
};

export default Sidebar;