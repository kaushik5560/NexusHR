import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import apiClient from '../api/client';
import { Users, LogOut, Clock, Calendar, ShieldCheck, CheckCircle2, AlertCircle } from 'lucide-react';

export default function DashboardPage() {
  const { user, logout } = useAuth();
  const [attendance, setAttendance] = useState<any>(null);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState<string | null>(null);

  // Fetch today's attendance status
  const fetchTodayAttendance = async () => {
    try {
      const response = await apiClient.get('/attendance/today');
      if (response.data && response.data.id) {
        setAttendance(response.data);
      }
    } catch (err) {
      console.error('Error fetching attendance', err);
    }
  };

  useEffect(() => {
    fetchTodayAttendance();
  }, []);

  // Live Check-In
  const handleCheckIn = async () => {
    setLoading(true);
    setMessage(null);
    try {
      const response = await apiClient.post('/attendance/check-in', { notes: 'Web Dashboard Check-in' });
      setAttendance(response.data);
      setMessage('Checked in successfully!');
    } catch (err: any) {
      setMessage(err.response?.data?.message || 'Check-in failed');
    } finally {
      setLoading(false);
    }
  };

  // Live Check-Out
  const handleCheckOut = async () => {
    setLoading(true);
    setMessage(null);
    try {
      const response = await apiClient.post('/attendance/check-out');
      setAttendance(response.data);
      setMessage('Checked out successfully!');
    } catch (err: any) {
      setMessage(err.response?.data?.message || 'Check-out failed');
    } finally {
      setLoading(false);
    }
  };

  const isHr = user?.roles.includes('ROLE_HR') || user?.roles.includes('ROLE_ADMIN');

  return (
    <div className="min-h-screen bg-slate-50 text-slate-800">
      {/* Top Navbar */}
      <nav className="bg-white border-b border-slate-200 sticky top-0 z-50">
        <div className="max-w-7xl mx-auto px-6 h-16 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="bg-blue-600 text-white p-2 rounded-xl shadow-md shadow-blue-500/20">
              <Users className="w-5 h-5" />
            </div>
            <span className="text-xl font-bold tracking-tight text-slate-900">
              Nexus<span className="text-blue-600">HR</span>
            </span>
            <span className="ml-2 text-xs font-semibold px-2 py-0.5 rounded-full bg-blue-50 text-blue-700 border border-blue-200">
              {isHr ? 'HR Portal' : 'Employee Self-Service'}
            </span>
          </div>

          <div className="flex items-center gap-4">
            <div className="text-right hidden sm:block">
              <p className="text-sm font-semibold text-slate-900">{user?.firstName} {user?.lastName}</p>
              <p className="text-xs text-slate-500">{user?.employeeCode} • {user?.roles[0]}</p>
            </div>
            <button
              onClick={logout}
              className="flex items-center gap-1.5 px-3 py-2 text-xs font-semibold text-red-600 hover:bg-red-50 rounded-xl transition-colors border border-red-100"
            >
              <LogOut className="w-4 h-4" />
              <span>Logout</span>
            </button>
          </div>
        </div>
      </nav>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto px-6 py-8">
        
        {/* Welcome Banner */}
        <div className="bg-gradient-to-r from-blue-600 to-indigo-700 rounded-3xl p-8 text-white shadow-xl shadow-blue-600/10 mb-8">
          <div className="max-w-3xl">
            <span className="text-xs font-semibold tracking-wider uppercase px-2.5 py-1 rounded-full bg-white/20 text-white backdrop-blur-md">
              Enterprise Dashboard
            </span>
            <h1 className="text-3xl font-bold tracking-tight mt-3">
              Welcome back, {user?.firstName}! 👋
            </h1>
            <p className="text-blue-100 text-sm mt-2">
              Employee ID: <span className="font-mono font-bold text-white">{user?.employeeCode}</span> • Work Email: {user?.email}
            </p>
          </div>
        </div>

        {message && (
          <div className="mb-6 p-4 rounded-xl bg-blue-50 border border-blue-200 text-blue-800 text-sm flex items-center gap-2">
            <CheckCircle2 className="w-4 h-4 text-blue-600" /> {message}
          </div>
        )}

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">

          {/* Quick Attendance Widget */}
          <div className="bg-white p-6 rounded-2xl border border-slate-200/80 shadow-sm">
            <div className="flex items-center justify-between mb-4">
              <div className="flex items-center gap-2 text-slate-900 font-bold">
                <Clock className="w-5 h-5 text-blue-600" />
                <span>Today's Attendance</span>
              </div>
              <span className={`text-xs font-bold px-2.5 py-1 rounded-full ${
                attendance?.status === 'PRESENT' ? 'bg-emerald-50 text-emerald-700 border border-emerald-200' :
                attendance?.status === 'LATE' ? 'bg-amber-50 text-amber-700 border border-amber-200' :
                'bg-slate-100 text-slate-600'
              }`}>
                {attendance?.status || 'NOT CHECKED IN'}
              </span>
            </div>

            <div className="space-y-3 text-sm text-slate-600 bg-slate-50 p-4 rounded-xl mb-5">
              <div className="flex justify-between">
                <span>Check In:</span>
                <span className="font-medium text-slate-900">{attendance?.checkInTime ? new Date(attendance.checkInTime).toLocaleTimeString() : '--:--'}</span>
              </div>
              <div className="flex justify-between">
                <span>Check Out:</span>
                <span className="font-medium text-slate-900">{attendance?.checkOutTime ? new Date(attendance.checkOutTime).toLocaleTimeString() : '--:--'}</span>
              </div>
              <div className="flex justify-between">
                <span>Work Hours:</span>
                <span className="font-bold text-blue-600">{attendance?.workHours ? `${attendance.workHours} hrs` : '--'}</span>
              </div>
            </div>

            <div className="grid grid-cols-2 gap-3">
              <button
                onClick={handleCheckIn}
                disabled={loading || !!attendance?.checkInTime}
                className="py-2.5 px-3 bg-emerald-600 hover:bg-emerald-700 disabled:opacity-50 text-white rounded-xl text-xs font-semibold shadow-sm transition-all"
              >
                Check In
              </button>
              <button
                onClick={handleCheckOut}
                disabled={loading || !attendance?.checkInTime || !!attendance?.checkOutTime}
                className="py-2.5 px-3 bg-slate-800 hover:bg-slate-900 disabled:opacity-50 text-white rounded-xl text-xs font-semibold shadow-sm transition-all"
              >
                Check Out
              </button>
            </div>
          </div>

          {/* Leave Management Card */}
          <div className="bg-white p-6 rounded-2xl border border-slate-200/80 shadow-sm">
            <div className="flex items-center gap-2 text-slate-900 font-bold mb-4">
              <Calendar className="w-5 h-5 text-indigo-600" />
              <span>Leave Summary</span>
            </div>
            <p className="text-sm text-slate-600 mb-6">
              Track your paid leave balance, upcoming planned vacations, and multi-tier approval states.
            </p>
            <div className="grid grid-cols-2 gap-3 mb-5">
              <div className="p-3.5 bg-blue-50/70 border border-blue-100 rounded-xl">
                <span className="text-xs text-blue-600 font-medium">Available Casual</span>
                <p className="text-2xl font-extrabold text-blue-900 mt-1">12 <span className="text-xs font-normal">days</span></p>
              </div>
              <div className="p-3.5 bg-purple-50/70 border border-purple-100 rounded-xl">
                <span className="text-xs text-purple-600 font-medium">Available Sick</span>
                <p className="text-2xl font-extrabold text-purple-900 mt-1">08 <span className="text-xs font-normal">days</span></p>
              </div>
            </div>
          </div>

          {/* Role Status Card */}
          <div className="bg-white p-6 rounded-2xl border border-slate-200/80 shadow-sm">
            <div className="flex items-center gap-2 text-slate-900 font-bold mb-4">
              <ShieldCheck className="w-5 h-5 text-emerald-600" />
              <span>Security & Access</span>
            </div>
            <div className="space-y-3 text-xs text-slate-600">
              <div className="p-3 rounded-xl bg-slate-50 flex items-center justify-between">
                <span>Account Status</span>
                <span className="font-bold text-emerald-600">ACTIVE</span>
              </div>
              <div className="p-3 rounded-xl bg-slate-50 flex items-center justify-between">
                <span>Session Type</span>
                <span className="font-semibold text-slate-800">Stateless JWT</span>
              </div>
              <div className="p-3 rounded-xl bg-slate-50 flex items-center justify-between">
                <span>Authorization Level</span>
                <span className="font-semibold text-blue-600">{user?.roles.join(', ')}</span>
              </div>
            </div>
          </div>

        </div>
      </main>
    </div>
  );
}