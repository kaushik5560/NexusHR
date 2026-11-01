export interface EmployeeProfile {
  id: number;
  employeeCode: string;
  firstName: string;
  lastName: string;
  email: string;
  phone?: string;
  department: string;
  designation: string;
  status: string;
  joiningDate?: string;
  hireDate?: string;
  roles: string[];
  salary?: number;
}

export interface AttendanceRecord {
  id: number;
  date: string;
  checkInTime?: string;
  checkOutTime?: string;
  durationHours?: number;
  workHours?: number;
  status: string;
  notes?: string;
}

export interface PayslipRecord {
  id: number;
  payslipReference: string;
  month: number;
  year: number;
  basicSalary: number;
  hra: number;
  allowances: number;
  specialAllowance: number;
  grossSalary: number;
  providentFund: number;
  pfDeduction: number;
  professionalTax: number;
  taxDeduction: number;
  totalDeductions: number;
  netSalary: number;
  paymentStatus: string;
  status: string;
  paymentDate?: string;
}

export interface ReviewRecord {
  id: number;
  reviewPeriod: string;
  reviewerName: string;
  technicalSkillsRating: number;
  communicationRating: number;
  productivityRating: number;
  leadershipRating: number;
  overallScore: number;
  keyAchievements?: string;
  areasOfImprovement?: string;
  managerFeedback?: string;
  status: string;
}

export interface LeaveRecord {
  id: number;
  employeeId?: number;
  employeeCode?: string;
  employeeName?: string;
  leaveType: string;
  startDate: string;
  endDate: string;
  durationDays: number;
  totalDays: number;
  reason: string;
  status: string;
  managerComment?: string;
  managerRemarks?: string;
  appliedAt?: string;
  reviewedAt?: string;
  reviewedBy?: string;
  reviewedByName?: string;
}

export interface AttritionRisk {
  employeeId: number;
  employeeCode: string;
  employeeName: string;
  department: string;
  designation: string;
  riskScore: number;
  riskLevel: 'LOW' | 'MODERATE' | 'HIGH' | 'CRITICAL' | string;
  confidenceScore: number;
  flightRiskWindow: string;
  primaryRiskFactors: string[];
  aiDiagnosis: string;
  retentionActions: string[];
}

export interface WorkforceAiOverview {
  totalEmployeesAnalyzed: number;
  lowRiskCount: number;
  moderateRiskCount: number;
  highRiskCount: number;
  criticalRiskCount: number;
  companyAverageRiskScore: number;
  highRiskEmployees: AttritionRisk[];
}

export interface CareerMilestone {
  timeframe: string;
  title: string;
  focusArea: string;
  recommendedAction: string;
}

export interface SkillGapAnalysis {
  employeeId: number;
  employeeName: string;
  currentRole: string;
  targetRole: string;
  readinessScore: number;
  readinessLevel: string;
  verifiedSkills: string[];
  identifiedSkillGaps: string[];
  learningPath: CareerMilestone[];
  aiCareerAdvice: string;
}

export interface NotificationItem {
  id: number;
  recipientEmail: string;
  title: string;
  message: string;
  type: 'LEAVE_APPLY' | 'LEAVE_ACTION' | 'PAYROLL' | 'REVIEW' | 'AI_ALERT' | 'SYSTEM' | string;
  isRead: boolean;
  createdAt: string;
}

export interface WorkforceAnalytics {
  totalEmployees: number;
  monthlyPayrollOutflow: number;
  averagePerformanceScore: number;
  retentionHealthScore: number;
  attendanceComplianceRate: number;
  departmentHeadcount: { [key: string]: number };
  attendanceStatusCounts: { [key: string]: number };
  pendingLeavesCount: number;
  approvedLeavesCount: number;
  leaveApprovalRate: number;
}

export interface DispatchLogItem {
  id: number;
  channel: 'EMAIL' | 'SMS' | string;
  recipient: string;
  subject: string;
  content: string;
  deliveryStatus: 'DELIVERED' | 'QUEUED' | 'FAILED' | string;
  latencyMs: number;
  dispatchedAt: string;
}
