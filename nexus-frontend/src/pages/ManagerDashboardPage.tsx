import React, { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import apiClient from '../api/client';
import NotificationDropdown from '../components/NotificationDropdown';
import ThemeToggle from '../components/ThemeToggle';
import type {
  EmployeeProfile,
  LeaveRecord,
  WorkforceAiOverview,
  WorkforceAnalytics,
  DispatchLogItem,
} from '../types/dashboard';
import {
  Users,
  CheckCircle2,
  XCircle,
  Clock,
  Calendar,
  DollarSign,
  ArrowLeft,
  AlertTriangle,
  Briefcase,
  ShieldCheck,
  Sparkles,
  Lightbulb,
  Printer,
  TrendingUp,
  BarChart3,
  Activity,
  Award,
  Send,
  Mail,
  Smartphone,
  Radio,
} from 'lucide-react';

export default function ManagerDashboardPage() {
  const { user } = useAuth();
  const navigate = useNavigate();

  const [employees, setEmployees] = useState<EmployeeProfile[]>([]);
  const [pendingLeaves, setPendingLeaves] = useState<LeaveRecord[]>([]);
  const [aiOverview, setAiOverview] = useState<WorkforceAiOverview | null>(null);
  const [analytics, setAnalytics] = useState<WorkforceAnalytics | null>(null);
  const [dispatchLogs, setDispatchLogs] = useState<DispatchLogItem[]>([]);

  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState<string | null>(null);

  // Remarks state for leaves
  const [remarks, setRemarks] = useState<{ [key: number]: string }>({});

  // Multi-Channel Broadcast Form State
  const [broadcastForm, setBroadcastForm] = useState({
    subject: '',
    content: '',
    channels: ['EMAIL', 'SMS'],
    targetAudience: 'ALL',
  });

  const loadManagerData = async () => {
    try {
      const [empRes, leaveRes, aiRes, analyticsRes, logRes] = await Promise.allSettled([
        apiClient.get('/employees'),
        apiClient.get('/leaves/pending'),
        apiClient.get('/ai/attrition/overview'),
        apiClient.get('/analytics/workforce-summary'),
        apiClient.get('/communications/audit-logs'),
      ]);

      if (empRes.status === 'fulfilled') setEmployees(empRes.value.data);
      if (leaveRes.status === 'fulfilled') setPendingLeaves(leaveRes.value.data);
      if (aiRes.status === 'fulfilled') setAiOverview(aiRes.value.data);
      if (analyticsRes.status === 'fulfilled') setAnalytics(analyticsRes.value.data);
      if (logRes.status === 'fulfilled') setDispatchLogs(logRes.value.data);
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
      const leaveRes = await apiClient.get('/leaves/pending');
      setPendingLeaves(leaveRes.data);

      const analyticsRes = await apiClient.get('/analytics/workforce-summary');
      setAnalytics(analyticsRes.data);
    } catch (err: any) {
      setMessage(err.response?.data?.message || 'Failed to process leave');
    } finally {
      setLoading(false);
    }
  };

  // Dispatch Multi-Channel Broadcast
  const handleSendBroadcast = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!broadcastForm.subject || !broadcastForm.content) return;
    setLoading(true);
    setMessage(null);
    try {
      await apiClient.post('/communications/broadcast', broadcastForm);
      setMessage('Multi-channel broadcast dispatched successfully across Email & SMS gateways!');
      setBroadcastForm({
        subject: '',
        content: '',
        channels: ['EMAIL', 'SMS'],
        targetAudience: 'ALL',
      });
      const logRes = await apiClient.get('/communications/audit-logs');
      setDispatchLogs(logRes.data);
    } catch (err: any) {
      setMessage(err.response?.data?.message || 'Failed to dispatch broadcast');
    } finally {
      setLoading(false);
    }
  };

  const handlePrintReport = () => {
    window.print();
  };

  return (
    <div className="min-h-screen bg-slate-50 dark:bg-slate-950 text-slate-800 dark:text-slate-100 transition-colors duration-200 print:bg-white print:text-black">
      {/* Top Navbar */}
      <nav className="bg-white dark:bg-slate-900 border-b border-slate-200 dark:border-slate-800 sticky top-0 z-50 print:hidden">
        <div className="max-w-7xl mx-auto px-6 h-16 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="bg-indigo-600 text-white p-2 rounded-xl shadow-md shadow-indigo-500/20">
              <ShieldCheck className="w-5 h-5" />
            </div>
            <span className="text-xl font-bold tracking-tight text-slate-900 dark:text-white">
              Nexus<span className="text-indigo-600">HR</span>
            </span>
            <span className="ml-2 text-xs font-semibold px-2.5 py-0.5 rounded-full bg-indigo-50 dark:bg-indigo-900/40 text-indigo-700 dark:text-indigo-300 border border-indigo-200 dark:border-indigo-800">
              Executive Manager Portal
            </span>
          </div>

          <div className="flex items-center gap-3">
            <ThemeToggle />
            <NotificationDropdown />
            <button
              onClick={handlePrintReport}
              className="flex items-center gap-1.5 px-3 py-1.5 text-xs font-semibold text-slate-700 dark:text-slate-200 hover:text-slate-900 dark:hover:text-white bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 dark:hover:bg-slate-700 rounded-xl transition-colors border border-slate-200 dark:border-slate-700 shadow-sm"
              title="Print Executive PDF Report"
            >
              <Printer className="w-4 h-4 text-indigo-600 dark:text-indigo-400" />
              <span>Export Report (PDF)</span>
            </button>
            <Link
              to="/dashboard"
              className="flex items-center gap-1.5 px-3 py-1.5 text-xs font-semibold text-slate-700 dark:text-slate-200 hover:text-slate-900 dark:hover:text-white bg-slate-100 dark:bg-slate-800 hover:bg-slate-200 dark:hover:bg-slate-700 rounded-xl transition-colors border border-slate-200 dark:border-slate-700"
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
        <div className="bg-gradient-to-r from-slate-900 via-indigo-950 to-slate-900 rounded-3xl p-8 text-white shadow-xl mb-8 flex flex-col md:flex-row md:items-center justify-between gap-6 print:rounded-none print:shadow-none print:border print:border-slate-300">
          <div>
            <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-indigo-500/30 text-indigo-200 text-xs font-semibold backdrop-blur-md mb-2">
              <Sparkles className="w-3.5 h-3.5" /> Department Administration & Control
            </div>
            <h1 className="text-3xl font-extrabold tracking-tight">
              Managerial Overview Console
            </h1>
            <p className="text-slate-300 text-sm mt-1">
              Active Administrator: <span className="font-semibold text-white">{user?.firstName} {user?.lastName}</span> ({user?.roles?.join(', ') || 'ROLE_HR'})
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
          <div className="mb-6 p-4 rounded-2xl bg-indigo-50 border border-indigo-200 text-indigo-900 text-sm flex items-center gap-2 shadow-sm print:hidden">
            <CheckCircle2 className="w-4 h-4 text-indigo-600 flex-shrink-0" />
            <span>{message}</span>
          </div>
        )}

        {/* 4 EXECUTIVE KPI CARDS */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5 mb-8">
          
          <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm">
            <div className="flex items-center justify-between mb-3">
              <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Payroll Outflow</span>
              <div className="w-8 h-8 rounded-lg bg-emerald-50 text-emerald-600 flex items-center justify-center">
                <DollarSign className="w-4 h-4" />
              </div>
            </div>
            <div className="text-xl font-bold text-slate-900">
              ₹{analytics ? analytics.monthlyPayrollOutflow.toLocaleString('en-IN') : '1,65,000'}
            </div>
            <p className="text-xs text-emerald-600 font-semibold mt-1">
              Monthly Active Run
            </p>
          </div>

          <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm">
            <div className="flex items-center justify-between mb-3">
              <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Retention Health</span>
              <div className="w-8 h-8 rounded-lg bg-blue-50 text-blue-600 flex items-center justify-center">
                <Activity className="w-4 h-4" />
              </div>
            </div>
            <div className="text-xl font-bold text-slate-900">
              {analytics ? `${analytics.retentionHealthScore}%` : '84.5%'}
            </div>
            <p className="text-xs text-blue-600 font-semibold mt-1">
              Workforce Stability Index
            </p>
          </div>

          <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm">
            <div className="flex items-center justify-between mb-3">
              <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Avg Performance</span>
              <div className="w-8 h-8 rounded-lg bg-amber-50 text-amber-500 flex items-center justify-center">
                <Award className="w-4 h-4" />
              </div>
            </div>
            <div className="text-xl font-bold text-slate-900">
              {analytics ? `${analytics.averagePerformanceScore}` : '4.5'} <span className="text-xs text-slate-400 font-normal">/ 5.0 ⭐</span>
            </div>
            <p className="text-xs text-amber-600 font-semibold mt-1">
              Calibrated Average
            </p>
          </div>

          <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm">
            <div className="flex items-center justify-between mb-3">
              <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Leave Governance</span>
              <div className="w-8 h-8 rounded-lg bg-purple-50 text-purple-600 flex items-center justify-center">
                <Calendar className="w-4 h-4" />
              </div>
            </div>
            <div className="text-xl font-bold text-purple-700">
              {analytics ? `${analytics.leaveApprovalRate}%` : '100%'}
            </div>
            <p className="text-xs text-slate-500 mt-1">
              {pendingLeaves.length} Pending • {analytics?.approvedLeavesCount || 2} Approved
            </p>
          </div>

        </div>

        {/* VISUAL WORKFORCE ANALYTICS SECTION */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-8">
          
          {/* Department Distribution */}
          <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm">
            <div className="flex items-center justify-between mb-4 pb-3 border-b border-slate-100">
              <div className="flex items-center gap-2">
                <BarChart3 className="w-5 h-5 text-indigo-600" />
                <h3 className="font-bold text-slate-900">Department Headcount Distribution</h3>
              </div>
              <span className="text-xs text-slate-400 font-mono">Total: {employees.length}</span>
            </div>

            <div className="space-y-4">
              {analytics && analytics.departmentHeadcount ? (
                Object.entries(analytics.departmentHeadcount).map(([dept, count]) => {
                  const percent = Math.round((count / (employees.length || 1)) * 100);
                  return (
                    <div key={dept} className="space-y-1.5 text-xs">
                      <div className="flex justify-between font-semibold">
                        <span className="text-slate-800">{dept}</span>
                        <span className="text-indigo-600">{count} Members ({percent}%)</span>
                      </div>
                      <div className="w-full bg-slate-100 h-2.5 rounded-full overflow-hidden">
                        <div
                          className="bg-indigo-600 h-full rounded-full transition-all duration-500"
                          style={{ width: `${percent}%` }}
                        ></div>
                      </div>
                    </div>
                  );
                })
              ) : (
                <div className="text-xs text-slate-400 py-4 text-center">Loading department distribution...</div>
              )}
            </div>
          </div>

          {/* Biometrics & Attendance Compliance */}
          <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm">
            <div className="flex items-center justify-between mb-4 pb-3 border-b border-slate-100">
              <div className="flex items-center gap-2">
                <Clock className="w-5 h-5 text-blue-600" />
                <h3 className="font-bold text-slate-900">Today's Attendance & Shift Compliance</h3>
              </div>
              <span className="text-xs font-bold text-emerald-600 bg-emerald-50 px-2.5 py-0.5 rounded-full">
                Active Shifts
              </span>
            </div>

            <div className="grid grid-cols-2 gap-4 text-xs">
              <div className="p-3.5 bg-slate-50 border border-slate-200 rounded-xl">
                <span className="text-slate-400">Compliance Rate</span>
                <p className="text-xl font-bold text-slate-900 mt-1">
                  {analytics ? `${analytics.attendanceComplianceRate}%` : '100%'}
                </p>
                <span className="text-[10px] text-emerald-600 font-medium">Logged biometric simulation</span>
              </div>

              <div className="p-3.5 bg-slate-50 border border-slate-200 rounded-xl">
                <span className="text-slate-400">Shift Schedule</span>
                <p className="text-xl font-bold text-slate-900 mt-1">09:30 AM</p>
                <span className="text-[10px] text-slate-500 font-medium">Standard 9-hr window</span>
              </div>
            </div>

            <div className="mt-4 pt-3 border-t border-slate-100 flex items-center justify-between text-xs text-slate-600">
              <span>Biometric Status:</span>
              <span className="font-semibold text-emerald-600">Real-Time Sync Active (HikariCP)</span>
            </div>
          </div>

        </div>

        {/* AI WORKFORCE FLIGHT RISK RADAR CARD */}
        {aiOverview && (
          <div className="bg-gradient-to-br from-indigo-900 via-indigo-950 to-slate-900 text-white rounded-3xl p-6 shadow-xl mb-8 border border-indigo-700/50">
            <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-indigo-800/60 pb-4 mb-6">
              <div className="flex items-center gap-3">
                <div className="p-2.5 rounded-xl bg-indigo-500/20 text-indigo-300 border border-indigo-500/30">
                  <Sparkles className="w-6 h-6 text-indigo-400" />
                </div>
                <div>
                  <h3 className="font-extrabold text-lg tracking-tight">NexusAI Workforce Flight Risk & Attrition Radar</h3>
                  <p className="text-xs text-indigo-200">Correlating Biometrics, Leave Requests, and 360° Scorecards</p>
                </div>
              </div>
              <div className="flex items-center gap-3">
                <div className="px-3.5 py-1.5 rounded-xl bg-white/10 backdrop-blur-md border border-white/15 text-xs">
                  <span className="text-indigo-200">Company Risk Avg:</span>{' '}
                  <span className="font-bold text-amber-300 font-mono">{aiOverview.companyAverageRiskScore}%</span>
                </div>
                <div className="px-3.5 py-1.5 rounded-xl bg-red-500/20 text-red-300 border border-red-500/30 text-xs font-bold">
                  {aiOverview.highRiskCount} High Risk Alert
                </div>
              </div>
            </div>

            {/* High Risk Employees Cards */}
            {aiOverview.highRiskEmployees.length === 0 ? (
              <p className="text-xs text-indigo-200 py-3 text-center">Zero high attrition risk alerts detected across the workforce.</p>
            ) : (
              <div className="space-y-4">
                {aiOverview.highRiskEmployees.map((risk) => (
                  <div key={risk.employeeId} className="bg-white/5 border border-white/10 rounded-2xl p-5 backdrop-blur-md">
                    <div className="flex flex-col sm:flex-row justify-between sm:items-center gap-3 mb-3 border-b border-white/10 pb-3">
                      <div>
                        <div className="flex items-center gap-2">
                          <AlertTriangle className="w-4 h-4 text-amber-400" />
                          <span className="font-bold text-base text-white">{risk.employeeName}</span>
                          <span className="font-mono text-xs text-indigo-300">({risk.employeeCode})</span>
                          <span className="px-2 py-0.5 rounded-full text-[10px] font-extrabold bg-red-500/30 text-red-300 border border-red-500/40">
                            {risk.riskLevel} RISK ({risk.riskScore}%)
                          </span>
                        </div>
                        <p className="text-xs text-indigo-200 mt-0.5">{risk.designation} • {risk.department}</p>
                      </div>
                      <div className="text-right">
                        <span className="text-xs font-semibold px-2.5 py-1 rounded-lg bg-amber-500/20 text-amber-300 border border-amber-500/30">
                          {risk.flightRiskWindow}
                        </span>
                      </div>
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-xs mt-3">
                      <div className="bg-black/20 p-3.5 rounded-xl border border-white/5 space-y-1.5">
                        <span className="font-bold text-indigo-300 uppercase tracking-wider text-[11px]">Primary Risk Drivers Identified:</span>
                        <ul className="space-y-1 mt-1 text-slate-300 list-disc list-inside">
                          {risk.primaryRiskFactors.map((factor, i) => (
                            <li key={i}>{factor}</li>
                          ))}
                        </ul>
                      </div>

                      <div className="bg-emerald-950/40 p-3.5 rounded-xl border border-emerald-500/20 space-y-1.5">
                        <span className="font-bold text-emerald-300 uppercase tracking-wider text-[11px] flex items-center gap-1.5">
                          <Lightbulb className="w-3.5 h-3.5 text-emerald-400" /> AI Retention Action Plan:
                        </span>
                        <ul className="space-y-1 mt-1 text-emerald-100 list-disc list-inside">
                          {risk.retentionActions.map((action, i) => (
                            <li key={i}>{action}</li>
                          ))}
                        </ul>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

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
                        {leave.leaveType} ({leave.totalDays || leave.durationDays || 1} Days)
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

        {/* SECTION 2: MULTI-CHANNEL BROADCAST & DISPATCH HUB (DAY 19) */}
        <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-6 mb-8 print:hidden">
          <div className="flex items-center justify-between mb-4 pb-3 border-b border-slate-100">
            <div className="flex items-center gap-2">
              <Radio className="w-5 h-5 text-purple-600" />
              <h3 className="font-bold text-slate-900">Multi-Channel Broadcast Hub (Email & SMS Service)</h3>
            </div>
            <span className="text-xs px-2.5 py-0.5 rounded-full bg-purple-50 text-purple-700 font-semibold border border-purple-200">
              F-07 Telecom & SMTP Gateways
            </span>
          </div>

          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            {/* Form */}
            <form onSubmit={handleSendBroadcast} className="space-y-3 text-xs lg:col-span-1 bg-slate-50 p-4 rounded-xl border border-slate-200">
              <div>
                <label className="block font-semibold text-slate-700 mb-1">Announcement Subject</label>
                <input
                  type="text"
                  required
                  placeholder="e.g. Townhall Meeting & Policy Updates"
                  value={broadcastForm.subject}
                  onChange={(e) => setBroadcastForm({ ...broadcastForm, subject: e.target.value })}
                  className="w-full p-2 border border-slate-300 rounded-lg text-xs bg-white text-slate-900"
                />
              </div>

              <div>
                <label className="block font-semibold text-slate-700 mb-1">Target Workforce</label>
                <select
                  value={broadcastForm.targetAudience}
                  onChange={(e) => setBroadcastForm({ ...broadcastForm, targetAudience: e.target.value })}
                  className="w-full p-2 border border-slate-300 rounded-lg text-xs bg-white text-slate-900"
                >
                  <option value="ALL">All Company Employees</option>
                  <option value="Engineering">Engineering Department</option>
                  <option value="Human Resources">Human Resources Department</option>
                </select>
              </div>

              <div>
                <label className="block font-semibold text-slate-700 mb-1">Channels</label>
                <div className="flex gap-4">
                  <label className="flex items-center gap-1.5 cursor-pointer">
                    <input
                      type="checkbox"
                      checked={broadcastForm.channels.includes('EMAIL')}
                      onChange={(e) => {
                        const newChannels = e.target.checked
                          ? [...broadcastForm.channels, 'EMAIL']
                          : broadcastForm.channels.filter((c) => c !== 'EMAIL');
                        setBroadcastForm({ ...broadcastForm, channels: newChannels });
                      }}
                      className="rounded border-slate-300 text-blue-600"
                    />
                    <span className="flex items-center gap-1"><Mail className="w-3.5 h-3.5 text-blue-600" /> Email</span>
                  </label>
                  <label className="flex items-center gap-1.5 cursor-pointer">
                    <input
                      type="checkbox"
                      checked={broadcastForm.channels.includes('SMS')}
                      onChange={(e) => {
                        const newChannels = e.target.checked
                          ? [...broadcastForm.channels, 'SMS']
                          : broadcastForm.channels.filter((c) => c !== 'SMS');
                        setBroadcastForm({ ...broadcastForm, channels: newChannels });
                      }}
                      className="rounded border-slate-300 text-emerald-600"
                    />
                    <span className="flex items-center gap-1"><Smartphone className="w-3.5 h-3.5 text-emerald-600" /> SMS</span>
                  </label>
                </div>
              </div>

              <div>
                <label className="block font-semibold text-slate-700 mb-1">Message Body</label>
                <textarea
                  required
                  rows={3}
                  placeholder="Enter broadcast message to dispatch..."
                  value={broadcastForm.content}
                  onChange={(e) => setBroadcastForm({ ...broadcastForm, content: e.target.value })}
                  className="w-full p-2 border border-slate-300 rounded-lg text-xs bg-white text-slate-900"
                />
              </div>

              <button
                type="submit"
                disabled={loading}
                className="w-full py-2 bg-purple-600 hover:bg-purple-700 text-white font-semibold rounded-lg text-xs transition-colors flex items-center justify-center gap-1.5 shadow-sm disabled:opacity-50"
              >
                <Send className="w-3.5 h-3.5" /> Dispatch Live Broadcast
              </button>
            </form>

            {/* Audit Logs Table */}
            <div className="lg:col-span-2 space-y-2">
              <div className="flex justify-between items-center mb-1">
                <span className="text-xs font-semibold text-slate-600">Live Gateway Transmission Ledger</span>
                <span className="text-[11px] text-slate-400 font-mono">99.8% Carrier SLA</span>
              </div>
              <div className="max-h-64 overflow-y-auto border border-slate-200 rounded-xl divide-y divide-slate-100">
                {dispatchLogs.length === 0 ? (
                  <div className="text-center py-8 text-xs text-slate-400">No broadcasts logged yet.</div>
                ) : (
                  dispatchLogs.map((log) => (
                    <div key={log.id} className="p-3 text-xs flex items-center justify-between gap-3 hover:bg-slate-50">
                      <div className="flex items-center gap-2 min-w-0">
                        <span className={`px-2 py-0.5 rounded font-bold text-[10px] ${
                          log.channel === 'EMAIL' ? 'bg-blue-100 text-blue-800' : 'bg-emerald-100 text-emerald-800'
                        }`}>
                          {log.channel}
                        </span>
                        <div className="min-w-0 truncate">
                          <p className="font-semibold text-slate-900 truncate">{log.subject}</p>
                          <p className="text-slate-500 text-[11px] truncate">To: {log.recipient}</p>
                        </div>
                      </div>
                      <div className="text-right flex-shrink-0">
                        <span className="px-2 py-0.5 rounded-full text-[10px] font-bold bg-emerald-50 text-emerald-700 border border-emerald-200">
                          {log.deliveryStatus} ({log.latencyMs}ms)
                        </span>
                        <span className="text-[10px] text-slate-400 block mt-0.5">
                          {new Date(log.dispatchedAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                        </span>
                      </div>
                    </div>
                  ))
                )}
              </div>
            </div>
          </div>
        </div>

        {/* SECTION 3: TEAM DIRECTORY TABLE */}
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
                        {emp.roles?.join(', ') || 'ROLE_EMPLOYEE'}
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
