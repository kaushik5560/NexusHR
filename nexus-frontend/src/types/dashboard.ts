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
  specialAllowance: number;      // Fixed: NOT optional to allow .toLocaleString()
  grossSalary: number;
  providentFund: number;
  pfDeduction: number;           // Fixed: NOT optional to allow .toLocaleString()
  professionalTax: number;
  taxDeduction: number;          // Fixed: NOT optional to allow .toLocaleString()
  totalDeductions: number;
  netSalary: number;
  paymentStatus: string;
  status: string;                // Fixed: NOT optional
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
  totalDays: number;             // Fixed: NOT optional
  reason: string;
  status: string;
  managerComment?: string;
  managerRemarks?: string;
  appliedAt?: string;
  reviewedAt?: string;
  reviewedBy?: string;
  reviewedByName?: string;
}