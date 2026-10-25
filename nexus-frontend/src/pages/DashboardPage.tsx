import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import apiClient from '../api/client';
import type {
  EmployeeProfile,
  AttendanceRecord,
  PayslipRecord,
  ReviewRecord,
  LeaveRecord,
} from '../types/dashboard';
import {
  Users,
  LogOut,
  Clock,
  Calendar,
  DollarSign,
  Star,
  CheckCircle2,
  AlertCircle,
  FileText,
  Send,
  Building,
  Briefcase,
  Sparkles,
} from 'lucide-react';

export default function DashboardPage() {
  const { user, logout } = useAuth();

  // State Management
  const [profile, setProfile] = useState<EmployeeProfile | null>(null);
  const [attendance, setAttendance] = useState<AttendanceRecord | null>(null);
  const [payslips, setPayslips] = useState<PayslipRecord[]>([]);
  const [reviews, setReviews] = useState<ReviewRecord[]>([]);
  const [leaves, setLeaves] = useState<LeaveRecord[]>([]);
  const [activeTab, setActiveTab] = useState<'overview' | 'payroll' | 'performance' | 'leaves'>('overview');

  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState<string | null>(null);

  // Leave Form State
  const [leaveForm, setLeaveForm] = useState({
    leaveType: 'CASUAL',
    startDate: '',
    endDate: '',
    reason: '',
  });

  // Fetch all employee workforce data in parallel from Spring Boot Backend
  const loadDashboardData = async () => {
    try {
      const [profileRes, attRes, payRes, revRes, leaveRes] = await Promise.allSettled([
        apiClient.get('/employees/me'),
        apiClient.get('/attendance/today'),
        apiClient.get('/payroll/my-payslips'),
        apiClient.get('/reviews/my-reviews'),
        apiClient.get('/leaves/my-leaves'),
      ]);

      if (profileRes.status === 'fulfilled') setProfile(profileRes.value.data);
      if (attRes.status === 'fulfilled' && attRes.value.data?.id) setAttendance(attRes.value.data);
      if (payRes.status === 'fulfilled') setPayslips(payRes.value.data);
      if (revRes.status === 'fulfilled') setReviews(revRes.value.data);
      if (leaveRes.status === 'fulfilled') setLeaves(leaveRes.value.data);
    } catch (err) {
      console.error('Failed to load dashboard data', err);
    }
  };

  useEffect(() => {
    loadDashboardData();
  }, []);

  // Attendance Actions
  const handleCheckIn = async () => {
    setLoading(true);
    setMessage(null);
    try {
      const res = await apiClient.post('/attendance/check-in', { notes: 'Web Dashboard Check-in' });
      setAttendance(res.data);
      setMessage('Checked in successfully!');
    } catch (err: any) {
      setMessage(err.response?.data?.message || 'Check-in failed');
    } finally {
      setLoading(false);
    }
  };

  const handleCheckOut = async () => {
    setLoading(true);
    setMessage(null);
    try {
      const res = await apiClient.post('/attendance/check-out');
      setAttendance(res.data);
      setMessage('Checked out successfully!');
    } catch (err: any) {
      setMessage(err.response?.data?.message || 'Check-out failed');
    } finally {
      setLoading(false);
    }
  };

  // Leave Form Submit
  const handleApplyLeave = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setMessage(null);
    try {
      await apiClient.post('/leaves/apply', leaveForm);
      setMessage('Leave application submitted successfully!');
      setLeaveForm({ leaveType: 'CASUAL', startDate: '', endDate: '', reason: '' });
      const leaveRes = await apiClient.get('/leaves/my-leaves');
      setLeaves(leaveRes.data);
    } catch (err: any) {
      setMessage(err.response?.data?.message || 'Failed to submit leave request');
    } finally {
      setLoading(false);
    }
  };

  const latestPayslip = payslips[0];
  const latestReview = reviews[0];

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
            <span className="ml-2 text-xs font-semibold px-2.5 py-0.5 rounded-full bg-blue-50 text-blue-700 border border-blue-200">
              Day 12: Employee Portal
            </span>
          </div>

          <div className="flex items-center gap-4">
            <div className="text-right hidden sm:block">
              <p className="text-sm font-semibold text-slate-900">{user?.firstName} {user?.lastName}</p>
              <p className="text-xs text-slate-500 font-mono">{user?.employeeCode} • {profile?.department || 'Engineering'}</p>
            </div>
            <button
              onClick={logout}
              className="flex items-center gap-1.5 px-3 py-1.5 text-xs font-semibold text-red-600 hover:bg-red-50 rounded-xl transition-colors border border-red-100"
            >
              <LogOut className="w-4 h-4" />
              <span>Sign Out</span>
            </button>
          </div>
        </div>
      </nav>

      {/* Main Container */}
      <main className="max-w-7xl mx-auto px-6 py-8">
        
        {/* Banner */}
        <div className="bg-gradient-to-r from-blue-600 via-indigo-600 to-indigo-700 rounded-3xl p-8 text-white shadow-xl shadow-blue-600/10 mb-8 flex flex-col md:flex-row md:items-center justify-between gap-6">
          <div>
            <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-white/20 text-white text-xs font-semibold backdrop-blur-md mb-2">
              <Sparkles className="w-3.5 h-3.5" /> Employee Self-Service Space
            </div>
            <h1 className="text-3xl font-extrabold tracking-tight">
              Hello, {user?.firstName} {user?.lastName}! 👋
            </h1>
            <p className="text-blue-100 text-sm mt-1">
              Role: <span className="font-semibold text-white">{profile?.designation || 'Software Engineer'}</span> • Department: <span className="font-semibold text-white">{profile?.department || 'Engineering'}</span>
            </p>
          </div>
          <div className="flex items-center gap-3">
            <div className="bg-white/10 backdrop-blur-md px-4 py-2.5 rounded-2xl border border-white/20">
              <p className="text-xs text-blue-200">Employee ID</p>
              <p className="text-base font-bold font-mono">{user?.employeeCode}</p>
            </div>
            <div className="bg-white/10 backdrop-blur-md px-4 py-2.5 rounded-2xl border border-white/20">
              <p className="text-xs text-blue-200">Status</p>
              <p className="text-base font-bold text-emerald-300">{profile?.status || 'ACTIVE'}</p>
            </div>
          </div>
        </div>

        {/* Action Alert Message */}
        {message && (
          <div className="mb-6 p-4 rounded-2xl bg-blue-50 border border-blue-200 text-blue-800 text-sm flex items-center gap-2 shadow-sm">
            <CheckCircle2 className="w-4 h-4 text-blue-600 flex-shrink-0" />
            <span>{message}</span>
          </div>
        )}

        {/* 4 CORE KPI METRIC CARDS */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5 mb-8">
          
          {/* KPI 1: Attendance */}
          <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm">
            <div className="flex items-center justify-between mb-3">
              <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Attendance Today</span>
              <div className="w-8 h-8 rounded-lg bg-blue-50 text-blue-600 flex items-center justify-center">
                <Clock className="w-4 h-4" />
              </div>
            </div>
            <div className="flex items-baseline gap-2">
              <span className={`text-xl font-bold ${
                attendance?.status === 'PRESENT' ? 'text-emerald-600' :
                attendance?.status === 'LATE' ? 'text-amber-600' : 'text-slate-700'
              }`}>
                {attendance?.status || 'Not Checked In'}
              </span>
            </div>
            <p className="text-xs text-slate-500 mt-1">
              Logged: {attendance?.workHours ? `${attendance.workHours} hrs` : '--'}
            </p>
          </div>

          {/* KPI 2: Latest Net Pay */}
          <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm">
            <div className="flex items-center justify-between mb-3">
              <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Latest Net Salary</span>
              <div className="w-8 h-8 rounded-lg bg-emerald-50 text-emerald-600 flex items-center justify-center">
                <DollarSign className="w-4 h-4" />
              </div>
            </div>
            <div className="text-xl font-bold text-slate-900">
              {latestPayslip ? `₹${latestPayslip.netSalary.toLocaleString('en-IN')}` : '₹0.00'}
            </div>
            <p className="text-xs text-emerald-600 font-semibold mt-1">
              {latestPayslip ? `Paid • Ref: ${latestPayslip.payslipReference}` : 'Pending calculation'}
            </p>
          </div>

          {/* KPI 3: Performance Rating */}
          <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm">
            <div className="flex items-center justify-between mb-3">
              <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider">360° Scorecard</span>
              <div className="w-8 h-8 rounded-lg bg-amber-50 text-amber-500 flex items-center justify-center">
                <Star className="w-4 h-4 fill-amber-400" />
              </div>
            </div>
            <div className="flex items-baseline gap-1.5">
              <span className="text-xl font-bold text-slate-900">
                {latestReview ? `${latestReview.overallScore}` : '4.5'}
              </span>
              <span className="text-xs text-slate-400 font-medium">/ 5.0 Stars</span>
            </div>
            <p className="text-xs text-slate-500 mt-1">
              Reviewed by {latestReview?.reviewerName || 'Priya Singh'}
            </p>
          </div>

          {/* KPI 4: Leave Status */}
          <div className="bg-white p-5 rounded-2xl border border-slate-200 shadow-sm">
            <div className="flex items-center justify-between mb-3">
              <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Leaves Status</span>
              <div className="w-8 h-8 rounded-lg bg-purple-50 text-purple-600 flex items-center justify-center">
                <Calendar className="w-4 h-4" />
              </div>
            </div>
            <div className="text-xl font-bold text-purple-700">
              {leaves.length > 0 ? `${leaves.filter(l => l.status === 'APPROVED').length} Approved` : '12 Days'}
            </div>
            <p className="text-xs text-slate-500 mt-1">
              Casual & Sick Leave Available
            </p>
          </div>

        </div>

        {/* NAVIGATION TABS */}
        <div className="flex gap-2 border-b border-slate-200 mb-6 pb-2 overflow-x-auto">
          {[
            { id: 'overview', label: 'Overview & Attendance', icon: Clock },
            { id: 'payroll', label: 'My Payslips', icon: DollarSign },
            { id: 'performance', label: 'Performance Scorecard', icon: Star },
            { id: 'leaves', label: 'Leave Portal', icon: Calendar },
          ].map((tab) => {
            const Icon = tab.icon;
            const active = activeTab === tab.id;
            return (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id as any)}
                className={`flex items-center gap-2 px-4 py-2.5 rounded-xl text-sm font-semibold transition-all ${
                  active
                    ? 'bg-blue-600 text-white shadow-md shadow-blue-500/20'
                    : 'text-slate-600 hover:text-slate-900 hover:bg-slate-100'
                }`}
              >
                <Icon className="w-4 h-4" />
                <span>{tab.label}</span>
              </button>
            );
          })}
        </div>

        {/* TAB 1: OVERVIEW & ATTENDANCE */}
        {activeTab === 'overview' && (
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            
            {/* Quick Check-in/out Card */}
            <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm">
              <h3 className="font-bold text-slate-900 flex items-center gap-2 mb-4">
                <Clock className="w-5 h-5 text-blue-600" /> Daily Biometric Simulation
              </h3>
              
              <div className="bg-slate-50 p-4 rounded-xl space-y-3 text-xs text-slate-600 mb-5">
                <div className="flex justify-between">
                  <span>Shift Schedule:</span>
                  <span className="font-semibold text-slate-900">09:30 AM - 06:30 PM</span>
                </div>
                <div className="flex justify-between">
                  <span>Check In:</span>
                  <span className="font-semibold text-slate-900">
                    {attendance?.checkInTime ? new Date(attendance.checkInTime).toLocaleTimeString() : '--:--'}
                  </span>
                </div>
                <div className="flex justify-between">
                  <span>Check Out:</span>
                  <span className="font-semibold text-slate-900">
                    {attendance?.checkOutTime ? new Date(attendance.checkOutTime).toLocaleTimeString() : '--:--'}
                  </span>
                </div>
                <div className="flex justify-between border-t border-slate-200 pt-2 font-bold">
                  <span>Total Work Hours:</span>
                  <span className="text-blue-600">{attendance?.workHours ? `${attendance.workHours} hrs` : '--'}</span>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <button
                  onClick={handleCheckIn}
                  disabled={loading || !!attendance?.checkInTime}
                  className="w-full py-2.5 bg-emerald-600 hover:bg-emerald-700 disabled:opacity-50 text-white rounded-xl text-xs font-semibold transition-all shadow-sm"
                >
                  Check In
                </button>
                <button
                  onClick={handleCheckOut}
                  disabled={loading || !attendance?.checkInTime || !!attendance?.checkOutTime}
                  className="w-full py-2.5 bg-slate-800 hover:bg-slate-900 disabled:opacity-50 text-white rounded-xl text-xs font-semibold transition-all shadow-sm"
                >
                  Check Out
                </button>
              </div>
            </div>

            {/* Profile Summary Card */}
            <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm lg:col-span-2">
              <h3 className="font-bold text-slate-900 flex items-center gap-2 mb-4">
                <Briefcase className="w-5 h-5 text-indigo-600" /> Employee Profile & Placement Details
              </h3>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-xs">
                <div className="p-3.5 bg-slate-50 rounded-xl">
                  <span className="text-slate-400">Full Name</span>
                  <p className="font-bold text-slate-900 text-sm mt-0.5">{profile?.firstName} {profile?.lastName}</p>
                </div>
                <div className="p-3.5 bg-slate-50 rounded-xl">
                  <span className="text-slate-400">Work Email</span>
                  <p className="font-bold text-slate-900 text-sm mt-0.5">{profile?.email}</p>
                </div>
                <div className="p-3.5 bg-slate-50 rounded-xl">
                  <span className="text-slate-400">Department</span>
                  <p className="font-bold text-slate-900 text-sm mt-0.5">{profile?.department}</p>
                </div>
                <div className="p-3.5 bg-slate-50 rounded-xl">
                  <span className="text-slate-400">Designation</span>
                  <p className="font-bold text-slate-900 text-sm mt-0.5">{profile?.designation}</p>
                </div>
                <div className="p-3.5 bg-slate-50 rounded-xl">
                  <span className="text-slate-400">Base Salary (CTC)</span>
                  <p className="font-bold text-slate-900 text-sm mt-0.5">₹{profile?.salary?.toLocaleString('en-IN') || '75,000.00'} / month</p>
                </div>
                <div className="p-3.5 bg-slate-50 rounded-xl">
                  <span className="text-slate-400">Onboarding Date</span>
                  <p className="font-bold text-slate-900 text-sm mt-0.5">{profile?.hireDate || '2026-10-23'}</p>
                </div>
              </div>
            </div>

          </div>
        )}

        {/* TAB 2: MY PAYSLIPS */}
        {activeTab === 'payroll' && (
          <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-6">
            <h3 className="font-bold text-slate-900 flex items-center gap-2 mb-4">
              <DollarSign className="w-5 h-5 text-emerald-600" /> Itemized Salary & Payslip History
            </h3>
            
            {payslips.length === 0 ? (
              <p className="text-sm text-slate-500 py-6 text-center">No payslips generated yet.</p>
            ) : (
              <div className="space-y-6">
                {payslips.map((pay) => (
                  <div key={pay.id} className="p-6 bg-slate-50 border border-slate-200 rounded-2xl">
                    <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-2 mb-4 border-b border-slate-200 pb-3">
                      <div>
                        <span className="text-xs font-bold font-mono px-2 py-0.5 bg-blue-100 text-blue-800 rounded-md">
                          {pay.payslipReference}
                        </span>
                        <h4 className="text-lg font-bold text-slate-900 mt-1">Payslip for Month {pay.month} / {pay.year}</h4>
                      </div>
                      <span className="text-xs font-bold px-3 py-1 bg-emerald-100 text-emerald-800 rounded-full">
                        {pay.status}
                      </span>
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6 text-xs">
                      {/* Earnings */}
                      <div className="bg-white p-4 rounded-xl border border-slate-200 space-y-2">
                        <h5 className="font-bold text-emerald-700 uppercase tracking-wider mb-2">Earnings (+)</h5>
                        <div className="flex justify-between"><span>Basic Salary (50%):</span><span className="font-mono font-semibold">₹{pay.basicSalary.toLocaleString('en-IN')}</span></div>
                        <div className="flex justify-between"><span>House Rent Allowance (HRA 40%):</span><span className="font-mono font-semibold">₹{pay.hra.toLocaleString('en-IN')}</span></div>
                        <div className="flex justify-between"><span>Special Allowance (20%):</span><span className="font-mono font-semibold">₹{pay.specialAllowance.toLocaleString('en-IN')}</span></div>
                        <div className="flex justify-between border-t border-slate-200 pt-2 font-bold text-slate-900">
                          <span>Gross Salary:</span>
                          <span className="font-mono text-emerald-600">₹{pay.grossSalary.toLocaleString('en-IN')}</span>
                        </div>
                      </div>

                      {/* Deductions */}
                      <div className="bg-white p-4 rounded-xl border border-slate-200 space-y-2">
                        <h5 className="font-bold text-red-700 uppercase tracking-wider mb-2">Deductions (-)</h5>
                        <div className="flex justify-between"><span>Provident Fund (PF 12%):</span><span className="font-mono font-semibold">₹{pay.pfDeduction.toLocaleString('en-IN')}</span></div>
                        <div className="flex justify-between"><span>Income Tax (TDS):</span><span className="font-mono font-semibold">₹{pay.taxDeduction.toLocaleString('en-IN')}</span></div>
                        <div className="flex justify-between border-t border-slate-200 pt-2 font-bold text-slate-900">
                          <span>Total Deductions:</span>
                          <span className="font-mono text-red-600">₹{pay.totalDeductions.toLocaleString('en-IN')}</span>
                        </div>
                      </div>
                    </div>

                    <div className="mt-4 p-4 bg-emerald-50 rounded-xl border border-emerald-200 flex justify-between items-center">
                      <div>
                        <span className="text-xs text-emerald-800 font-medium">Net Take-Home Salary</span>
                        <p className="text-2xl font-extrabold text-emerald-900">₹{pay.netSalary.toLocaleString('en-IN')}</p>
                      </div>
                      <span className="text-xs text-slate-500 font-medium">Disbursed on {pay.paymentDate}</span>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {/* TAB 3: PERFORMANCE SCORECARD */}
        {activeTab === 'performance' && (
          <div className="bg-white rounded-2xl border border-slate-200 shadow-sm p-6">
            <h3 className="font-bold text-slate-900 flex items-center gap-2 mb-6">
              <Star className="w-5 h-5 text-amber-500 fill-amber-400" /> 360-Degree Performance Scorecards
            </h3>

            {reviews.length === 0 ? (
              <p className="text-sm text-slate-500 py-6 text-center">No performance reviews finalized yet.</p>
            ) : (
              <div className="space-y-6">
                {reviews.map((rev) => (
                  <div key={rev.id} className="p-6 bg-slate-50 border border-slate-200 rounded-2xl">
                    <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-2 mb-6 border-b border-slate-200 pb-4">
                      <div>
                        <span className="text-xs font-bold px-2.5 py-1 bg-amber-100 text-amber-800 rounded-lg">
                          Period: {rev.reviewPeriod}
                        </span>
                        <h4 className="text-lg font-bold text-slate-900 mt-2">Executive Evaluation Scorecard</h4>
                        <p className="text-xs text-slate-500">Evaluated by: <span className="font-semibold text-slate-800">{rev.reviewerName}</span></p>
                      </div>
                      <div className="text-center sm:text-right">
                        <span className="text-3xl font-extrabold text-slate-900">{rev.overallScore}</span>
                        <span className="text-sm text-slate-500"> / 5.0 ⭐</span>
                      </div>
                    </div>

                    {/* 4 Pillars Progress Bars */}
                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 mb-6">
                      <div className="bg-white p-3.5 rounded-xl border border-slate-200">
                        <div className="flex justify-between text-xs font-semibold mb-1">
                          <span>Technical Skills</span>
                          <span className="text-blue-600">{rev.technicalSkillsRating} / 5.0</span>
                        </div>
                        <div className="w-full bg-slate-100 h-2 rounded-full overflow-hidden">
                          <div className="bg-blue-600 h-full rounded-full" style={{ width: `${(rev.technicalSkillsRating / 5) * 100}%` }}></div>
                        </div>
                      </div>

                      <div className="bg-white p-3.5 rounded-xl border border-slate-200">
                        <div className="flex justify-between text-xs font-semibold mb-1">
                          <span>Communication</span>
                          <span className="text-indigo-600">{rev.communicationRating} / 5.0</span>
                        </div>
                        <div className="w-full bg-slate-100 h-2 rounded-full overflow-hidden">
                          <div className="bg-indigo-600 h-full rounded-full" style={{ width: `${(rev.communicationRating / 5) * 100}%` }}></div>
                        </div>
                      </div>

                      <div className="bg-white p-3.5 rounded-xl border border-slate-200">
                        <div className="flex justify-between text-xs font-semibold mb-1">
                          <span>Productivity & Delivery</span>
                          <span className="text-emerald-600">{rev.productivityRating} / 5.0</span>
                        </div>
                        <div className="w-full bg-slate-100 h-2 rounded-full overflow-hidden">
                          <div className="bg-emerald-600 h-full rounded-full" style={{ width: `${(rev.productivityRating / 5) * 100}%` }}></div>
                        </div>
                      </div>

                      <div className="bg-white p-3.5 rounded-xl border border-slate-200">
                        <div className="flex justify-between text-xs font-semibold mb-1">
                          <span>Leadership & Initiative</span>
                          <span className="text-purple-600">{rev.leadershipRating} / 5.0</span>
                        </div>
                        <div className="w-full bg-slate-100 h-2 rounded-full overflow-hidden">
                          <div className="bg-purple-600 h-full rounded-full" style={{ width: `${(rev.leadershipRating / 5) * 100}%` }}></div>
                        </div>
                      </div>
                    </div>

                    {/* Qualitative Notes */}
                    <div className="space-y-3 text-xs">
                      <div className="p-3 bg-white rounded-xl border border-slate-200">
                        <span className="font-bold text-slate-800">Key Achievements:</span>
                        <p className="text-slate-600 mt-1">{rev.keyAchievements}</p>
                      </div>
                      <div className="p-3 bg-white rounded-xl border border-slate-200">
                        <span className="font-bold text-slate-800">Areas for Growth:</span>
                        <p className="text-slate-600 mt-1">{rev.areasOfImprovement}</p>
                      </div>
                      <div className="p-3.5 bg-blue-50/70 border border-blue-200 rounded-xl">
                        <span className="font-bold text-blue-900">Manager's Executive Feedback:</span>
                        <p className="text-blue-800 mt-1 font-medium">{rev.managerFeedback}</p>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {/* TAB 4: LEAVE PORTAL */}
        {activeTab === 'leaves' && (
          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            
            {/* Apply Leave Form */}
            <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm">
              <h3 className="font-bold text-slate-900 flex items-center gap-2 mb-4">
                <Calendar className="w-5 h-5 text-purple-600" /> Apply For Time-Off
              </h3>
              <form onSubmit={handleApplyLeave} className="space-y-3 text-xs">
                <div>
                  <label className="block font-semibold text-slate-700 mb-1">Leave Category</label>
                  <select
                    value={leaveForm.leaveType}
                    onChange={(e) => setLeaveForm({ ...leaveForm, leaveType: e.target.value })}
                    className="w-full p-2.5 border border-slate-300 rounded-xl text-xs bg-white text-slate-800"
                  >
                    <option value="CASUAL">Casual Leave (Paid)</option>
                    <option value="SICK">Sick Leave (Medical)</option>
                    <option value="ANNUAL">Annual Planned Vacation</option>
                  </select>
                </div>

                <div className="grid grid-cols-2 gap-2">
                  <div>
                    <label className="block font-semibold text-slate-700 mb-1">Start Date</label>
                    <input
                      type="date"
                      required
                      value={leaveForm.startDate}
                      onChange={(e) => setLeaveForm({ ...leaveForm, startDate: e.target.value })}
                      className="w-full p-2 border border-slate-300 rounded-xl text-xs"
                    />
                  </div>
                  <div>
                    <label className="block font-semibold text-slate-700 mb-1">End Date</label>
                    <input
                      type="date"
                      required
                      value={leaveForm.endDate}
                      onChange={(e) => setLeaveForm({ ...leaveForm, endDate: e.target.value })}
                      className="w-full p-2 border border-slate-300 rounded-xl text-xs"
                    />
                  </div>
                </div>

                <div>
                  <label className="block font-semibold text-slate-700 mb-1">Reason</label>
                  <textarea
                    required
                    rows={3}
                    placeholder="Brief description of leave..."
                    value={leaveForm.reason}
                    onChange={(e) => setLeaveForm({ ...leaveForm, reason: e.target.value })}
                    className="w-full p-2 border border-slate-300 rounded-xl text-xs text-slate-800"
                  />
                </div>

                <button
                  type="submit"
                  disabled={loading}
                  className="w-full py-2.5 bg-blue-600 hover:bg-blue-700 text-white font-semibold rounded-xl text-xs transition-all shadow-sm"
                >
                  Submit Application
                </button>
              </form>
            </div>

            {/* Leave Applications History */}
            <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm lg:col-span-2">
              <h3 className="font-bold text-slate-900 flex items-center gap-2 mb-4">
                <FileText className="w-5 h-5 text-indigo-600" /> Past Applications & Approval States
              </h3>

              {leaves.length === 0 ? (
                <p className="text-xs text-slate-500 py-6 text-center">No leave applications recorded.</p>
              ) : (
                <div className="space-y-3">
                  {leaves.map((l) => (
                    <div key={l.id} className="p-4 bg-slate-50 border border-slate-200 rounded-xl text-xs space-y-1.5">
                      <div className="flex justify-between items-center">
                        <span className="font-bold text-slate-900">{l.leaveType} LEAVE ({l.totalDays} Days)</span>
                        <span className={`px-2.5 py-0.5 rounded-full font-bold ${
                          l.status === 'APPROVED' ? 'bg-emerald-100 text-emerald-800' :
                          l.status === 'REJECTED' ? 'bg-red-100 text-red-800' : 'bg-amber-100 text-amber-800'
                        }`}>
                          {l.status}
                        </span>
                      </div>
                      <p className="text-slate-500">Dates: <span className="font-semibold text-slate-700">{l.startDate} to {l.endDate}</span></p>
                      <p className="text-slate-600">Reason: {l.reason}</p>
                      {l.reviewedByName && (
                        <div className="pt-1.5 border-t border-slate-200 text-slate-500">
                          Reviewed by <span className="font-semibold text-slate-700">{l.reviewedByName}</span>: "{l.managerRemarks}"
                        </div>
                      )}
                    </div>
                  ))}
                </div>
              )}
            </div>

          </div>
        )}

      </main>
    </div>
  );
}