import React, { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import apiClient from '../api/client';
import type { EmployeeProfile, LeaveRecord } from '../types/dashboard';
import {
  Users,
  CheckCircle2,
  XCircle,
  Clock,
  Calendar,
  DollarSign,
  ArrowLeft,
  AlertCircle,
  Briefcase,
  ShieldCheck,
  Sparkles,
} from 'lucide-react';

export default function ManagerDashboardPage() {
  const { user } = useAuth();
  const navigate = useNavigate();

  const [employees, setEmployees] = useState<EmployeeProfile[]>([]);
  const [pendingLeaves, setPendingLeaves] = useState<LeaveRecord[]>([]);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState<string | null>(null);

  // Remarks modal/inline state
  const [remarks, setRemarks] = useState<{ [key: number]: string }>({});

  const loadManagerData = async () => {
    try {
      const [empRes, leaveRes] = await Promise.allSettled([
        apiClient.get('/employees'),
        apiClient.get('/leaves/pending'),
      ]);

      if (empRes.status === 'fulfilled') setEmployees(empRes.value.data);
      if (leaveRes.status === 'fulfilled') setPendingLeaves(leaveRes.value.data);
    } catch (err) {
      console.error('Failed to load manager data', err);
    }
  };

  useEffect(() => {
    loadManagerData();
  }, []);

  // Process Leave Action (Approve / Reject)
  const handleProcessLeave = async (leaveId: number, status: 'APPROVED' | 'REJECTED') => {
    setLoading(true);
    setMessage(null);
    try {
      const remarkText = remarks[leaveId] || (status === 'APPROVED' ? 'Approved by HR Lead' : 'Rejected per policy');
      await apiClient.put(`/leaves/${leaveId}/process`, {
        status,
        remarks: remarkText,
      });

      setMessage(`Leave request #${leaveId} has been successfully ${status}!`);
      // Refresh pending leaves list
      const leaveRes = await apiClient.get('/leaves/pending');
      setPendingLeaves(leaveRes.data);
    } catch (err: any) {
      setMessage(err.response?.data?.message || 'Failed to process leave');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-slate-50 text-slate-800">
      {/* Top Navbar */}
      <nav className="bg-white border-b border-slate-200 sticky top-0 z-50">
        <div className="max-w-7xl mx-auto px-6 h-16 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="bg-indigo-600 text-white p-2 rounded-xl shadow-md shadow-indigo-500/20">
              <ShieldCheck className="w-5 h-5" />
            </div>
            <span className="text-xl font-bold tracking-tight text-slate-900">
              Nexus<span className="text-indigo-600">HR</span>
            </span>
            <span className="ml-2 text-xs font-semibold px-2.5 py-0.5 rounded-full bg-indigo-50 text-indigo-700 border border-indigo-200">
              Executive Manager Portal
            </span>
          </div>

          <div className="flex items-center gap-4">
            <Link
              to="/dashboard"
              className="flex items-center gap-1.5 px-3 py-1.5 text-xs font-semibold text-slate-700 hover:text-slate-900 bg-slate-100 hover:bg-slate-200 rounded-xl transition-colors"
            >
              <ArrowLeft className="w-4 h-4" />
              <span>Back to Employee View</span>
            </Link>
          </div>
        </div>
      </nav>

      {/* Main Container */}
      <main className="max-w-7xl mx-auto px-6 py-8">

        {/* Manager Header Banner */}
        <div className="bg-gradient-to-r from-slate-900 via-indigo-950 to-slate-900 rounded-3xl p-8 text-white shadow-xl mb-8 flex flex-col md:flex-row md:items-center justify-between gap-6">
          <div>
            <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-indigo-500/30 text-indigo-200 text-xs font-semibold backdrop-blur-md mb-2">
              <Sparkles className="w-3.5 h-3.5" /> Department Administration & Control
            </div>
            <h1 className="text-3xl font-extrabold tracking-tight">
              Managerial Overview Console
            </h1>
            <p className="text-slate-300 text-sm mt-1">
              Active Administrator: <span className="font-semibold text-white">{user?.firstName} {user?.lastName}</span> ({user?.roles.join(', ')})
            </p>
          </div>
          <div className="flex items-center gap-3">
            <div className="bg-white/10 backdrop-blur-md px-4 py-2.5 rounded-2xl border border-white/20">
              <p className="text-xs text-slate-400">Total Workforce</p>
              <p className="text-2xl font-bold font-mono">{employees.length}</p>
            </div>
            <div className="bg-white/10 backdrop-blur-md px-4 py-2.5 rounded-2xl border border-white/20">
              <p className="text-xs text-slate-400">Action Queue</p>
              <p className="text-2xl font-bold text-amber-300 font-mono">{pendingLeaves.length}</p>
            </div>
          </div>
        </div>

        {message && (
          <div className="mb-6 p-4 rounded-2xl bg-indigo-50 border border-indigo-200 text-indigo-900 text-sm flex items-center gap-2 shadow-sm">
            <CheckCircle2 className="w-4 h-4 text-indigo-600 flex-shrink-0" />
            <span>{message}</span>
          </div>
        )}

        {/* 3 STATS OVERVIEW CARDS */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-5 mb-8">
          <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm flex items-center gap-4">
            <div className="w-12 h-12 rounded-xl bg-blue-50 text-blue-600 flex items-center justify-center flex-shrink-0">
              <Users className="w-6 h-6" />
            </div>
            <div>
              <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Active Employees</span>
              <p className="text-2xl font-extrabold text-slate-900 mt-0.5">{employees.length} Members</p>
            </div>
          </div>

          <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm flex items-center gap-4">
            <div className="w-12 h-12 rounded-xl bg-amber-50 text-amber-600 flex items-center justify-center flex-shrink-0">
              <Calendar className="w-6 h-6" />
            </div>
            <div>
              <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Pending Leave Reviews</span>
              <p className="text-2xl font-extrabold text-amber-600 mt-0.5">{pendingLeaves.length} Applications</p>
            </div>
          </div>

          <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm flex items-center gap-4">
            <div className="w-12 h-12 rounded-xl bg-emerald-50 text-emerald-600 flex items-center justify-center flex-shrink-0">
              <DollarSign className="w-6 h-6" />
            </div>
            <div>
              <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Payroll Processing</span>
              <p className="text-2xl font-extrabold text-emerald-600 mt-0.5">Automated Engine</p>
            </div>
          </div>
        </div>

        {/* SECTION 1: PENDING LEAVE APPROVALS QUEUE */}
        <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-6 mb-8">
          <div className="flex items-center justify-between mb-4 border-b border-slate-100 pb-3">
            <div className="flex items-center gap-2">
              <Clock className="w-5 h-5 text-amber-500" />
              <h3 className="font-bold text-slate-900">Leave Approval Queue (Awaiting Decision)</h3>
            </div>
            <span className="text-xs font-bold px-2.5 py-0.5 bg-amber-100 text-amber-800 rounded-full">
              {pendingLeaves.length} Pending
            </span>
          </div>

          {pendingLeaves.length === 0 ? (
            <div className="text-center py-8 text-slate-400 text-xs">
              <CheckCircle2 className="w-8 h-8 mx-auto text-emerald-400 mb-2" />
              All caught up! Zero pending leave applications requiring approval.
            </div>
          ) : (
            <div className="space-y-4">
              {pendingLeaves.map((leave) => (
                <div key={leave.id} className="p-4 bg-slate-50 border border-slate-200 rounded-xl flex flex-col md:flex-row md:items-center justify-between gap-4 text-xs">
                  <div>
                    <div className="flex items-center gap-2 mb-1">
                      <span className="font-bold text-slate-900 text-sm">{leave.employeeName}</span>
                      <span className="font-mono text-slate-500">({leave.employeeCode})</span>
                      <span className="px-2 py-0.5 rounded bg-blue-100 text-blue-800 font-semibold text-[10px]">
                        {leave.leaveType} ({leave.totalDays} Days)
                      </span>
                    </div>
                    <p className="text-slate-600">Period: <span className="font-medium text-slate-900">{leave.startDate} to {leave.endDate}</span></p>
                    <p className="text-slate-500 italic mt-0.5">"{leave.reason}"</p>
                  </div>

                  <div className="flex items-center gap-2 flex-shrink-0">
                    <input
                      type="text"
                      placeholder="Optional remarks..."
                      value={remarks[leave.id] || ''}
                      onChange={(e) => setRemarks({ ...remarks, [leave.id]: e.target.value })}
                      className="px-2.5 py-1.5 border border-slate-300 rounded-lg text-xs bg-white text-slate-800 w-44"
                    />
                    <button
                      onClick={() => handleProcessLeave(leave.id, 'APPROVED')}
                      disabled={loading}
                      className="flex items-center gap-1 px-3 py-1.5 bg-emerald-600 hover:bg-emerald-700 text-white font-semibold rounded-lg text-xs transition-colors shadow-sm disabled:opacity-50"
                    >
                      <CheckCircle2 className="w-3.5 h-3.5" /> Approve
                    </button>
                    <button
                      onClick={() => handleProcessLeave(leave.id, 'REJECTED')}
                      disabled={loading}
                      className="flex items-center gap-1 px-3 py-1.5 bg-red-600 hover:bg-red-700 text-white font-semibold rounded-lg text-xs transition-colors shadow-sm disabled:opacity-50"
                    >
                      <XCircle className="w-3.5 h-3.5" /> Reject
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* SECTION 2: TEAM DIRECTORY TABLE */}
        <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-6">
          <div className="flex items-center justify-between mb-4 border-b border-slate-100 pb-3">
            <div className="flex items-center gap-2">
              <Briefcase className="w-5 h-5 text-indigo-600" />
              <h3 className="font-bold text-slate-900">Workforce Directory (All Members)</h3>
            </div>
            <span className="text-xs text-slate-500 font-medium">PostgreSQL 17 Synchronized</span>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="bg-slate-50 text-slate-500 uppercase tracking-wider border-b border-slate-200">
                <tr>
                  <th className="py-3 px-4 font-semibold">Employee</th>
                  <th className="py-3 px-4 font-semibold">Code</th>
                  <th className="py-3 px-4 font-semibold">Department</th>
                  <th className="py-3 px-4 font-semibold">Designation</th>
                  <th className="py-3 px-4 font-semibold">Base Salary</th>
                  <th className="py-3 px-4 font-semibold">Status</th>
                  <th className="py-3 px-4 font-semibold">Roles</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {employees.map((emp) => (
                  <tr key={emp.id} className="hover:bg-slate-50/80 transition-colors">
                    <td className="py-3.5 px-4">
                      <p className="font-bold text-slate-900">{emp.firstName} {emp.lastName}</p>
                      <p className="text-[11px] text-slate-400">{emp.email}</p>
                    </td>
                    <td className="py-3.5 px-4 font-mono font-semibold text-slate-700">{emp.employeeCode}</td>
                    <td className="py-3.5 px-4 font-medium text-slate-800">{emp.department}</td>
                    <td className="py-3.5 px-4 text-slate-600">{emp.designation}</td>
                    <td className="py-3.5 px-4 font-mono font-semibold text-emerald-600">
                      ₹{emp.salary ? emp.salary.toLocaleString('en-IN') : '75,000'}
                    </td>
                    <td className="py-3.5 px-4">
                      <span className="px-2 py-0.5 rounded-full text-[10px] font-bold bg-emerald-50 text-emerald-700 border border-emerald-200">
                        {emp.status}
                      </span>
                    </td>
                    <td className="py-3.5 px-4">
                      <span className="px-2 py-0.5 rounded-md text-[10px] font-semibold bg-slate-100 text-slate-700">
                        {emp.roles.join(', ')}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

      </main>
    </div>
  );
}