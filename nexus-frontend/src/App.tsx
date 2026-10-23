import React from 'react';
import { ShieldCheck, Users, Clock, Calendar, Sparkles, Database } from 'lucide-react';

export default function App() {
  return (
    <div className="min-h-screen bg-slate-50 text-slate-800">
      {/* Top Navigation */}
      <header className="bg-white border-b border-slate-200 sticky top-0 z-50">
        <div className="max-w-7xl mx-auto px-6 h-16 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div className="bg-blue-600 text-white p-2 rounded-xl shadow-md shadow-blue-500/20">
              <Users className="w-5 h-5" />
            </div>
            <div>
              <span className="text-xl font-bold tracking-tight text-slate-900">Nexus<span className="text-blue-600">HR</span></span>
              <span className="ml-2 text-xs font-semibold px-2 py-0.5 rounded-full bg-blue-50 text-blue-700 border border-blue-200">
                Enterprise AI
              </span>
            </div>
          </div>
          <div className="flex items-center gap-3">
            <button className="px-4 py-2 text-sm font-medium text-slate-700 hover:text-slate-900 transition-colors">
              Documentation
            </button>
            <button className="px-4 py-2 text-sm font-medium bg-blue-600 hover:bg-blue-700 text-white rounded-lg shadow-sm shadow-blue-500/20 transition-all">
              Sign In
            </button>
          </div>
        </div>
      </header>

      {/* Hero Section */}
      <main className="max-w-7xl mx-auto px-6 py-12">
        <div className="text-center max-w-3xl mx-auto mb-12">
          <div className="inline-flex items-center gap-2 px-3 py-1.5 rounded-full bg-blue-50 border border-blue-200 text-blue-700 text-xs font-semibold mb-4">
            <Sparkles className="w-3.5 h-3.5 text-blue-600" /> Day 8: Frontend Foundation Operational
          </div>
          <h1 className="text-4xl sm:text-5xl font-extrabold text-slate-900 tracking-tight leading-tight">
            Next-Gen Workforce & <span className="text-blue-600">HR Intelligence</span>
          </h1>
          <p className="mt-4 text-base sm:text-lg text-slate-600 leading-relaxed">
            Production-grade Java Full Stack platform powered by Spring Boot 4, PostgreSQL 17, and modern React 19.
          </p>
        </div>

        {/* System Capability Cards */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          <div className="bg-white p-6 rounded-2xl border border-slate-200/80 shadow-sm hover:shadow-md transition-shadow">
            <div className="w-10 h-10 rounded-xl bg-indigo-50 text-indigo-600 flex items-center justify-center mb-4">
              <ShieldCheck className="w-5 h-5" />
            </div>
            <h3 className="font-semibold text-slate-900">Argon2 Security</h3>
            <p className="text-sm text-slate-500 mt-1">Cryptographic JWT token verification & RBAC authorization.</p>
            <div className="mt-4 text-xs font-semibold text-emerald-600 bg-emerald-50 px-2.5 py-1 rounded-md inline-block">
              Backend Verified ✅
            </div>
          </div>

          <div className="bg-white p-6 rounded-2xl border border-slate-200/80 shadow-sm hover:shadow-md transition-shadow">
            <div className="w-10 h-10 rounded-xl bg-blue-50 text-blue-600 flex items-center justify-center mb-4">
              <Clock className="w-5 h-5" />
            </div>
            <h3 className="font-semibold text-slate-900">Smart Attendance</h3>
            <p className="text-sm text-slate-500 mt-1">Biometric check-in/out with automated working hour calculation.</p>
            <div className="mt-4 text-xs font-semibold text-emerald-600 bg-emerald-50 px-2.5 py-1 rounded-md inline-block">
              Backend Verified ✅
            </div>
          </div>

          <div className="bg-white p-6 rounded-2xl border border-slate-200/80 shadow-sm hover:shadow-md transition-shadow">
            <div className="w-10 h-10 rounded-xl bg-purple-50 text-purple-600 flex items-center justify-center mb-4">
              <Calendar className="w-5 h-5" />
            </div>
            <h3 className="font-semibold text-slate-900">Leave Workflows</h3>
            <p className="text-sm text-slate-500 mt-1">Multi-tier approval hierarchy for Managers and HR teams.</p>
            <div className="mt-4 text-xs font-semibold text-emerald-600 bg-emerald-50 px-2.5 py-1 rounded-md inline-block">
              Backend Verified ✅
            </div>
          </div>

          <div className="bg-white p-6 rounded-2xl border border-slate-200/80 shadow-sm hover:shadow-md transition-shadow">
            <div className="w-10 h-10 rounded-xl bg-amber-50 text-amber-600 flex items-center justify-center mb-4">
              <Database className="w-5 h-5" />
            </div>
            <h3 className="font-semibold text-slate-900">PostgreSQL 17</h3>
            <p className="text-sm text-slate-500 mt-1">ACID compliant relational data architecture with JPA Hibernate.</p>
            <div className="mt-4 text-xs font-semibold text-emerald-600 bg-emerald-50 px-2.5 py-1 rounded-md inline-block">
              Connected ✅
            </div>
          </div>
        </div>
      </main>
    </div>
  );
}