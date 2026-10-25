export interface EmployeeProfile {
  id: number;
  employeeCode: string;
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber?: string;
  department: string;
  designation: string;
  salary: number;
  status: string;
  hireDate: string;
  roles: string[];
}

export interface AttendanceRecord {
  id: number;
  date: string;
  checkInTime: string | null;
  checkOutTime: string | null;
  workHours: number | null;
  status: string;
  notes?: string;
}

export interface PayslipRecord {
  id: number;
  month: number;
  year: number;
  basicSalary: number;
  hra: number;
  specialAllowance: number;
  grossSalary: number;
  pfDeduction: number;
  taxDeduction: number;
  totalDeductions: number;
  netSalary: number;
  status: string;
  paymentDate: string;
  payslipReference: string;
}

export interface ReviewRecord {
  id: number;
  reviewerName: string;
  reviewPeriod: string;
  technicalSkillsRating: number;
  communicationRating: number;
  productivityRating: number;
  leadershipRating: number;
  overallScore: number;
  keyAchievements: string;
  areasOfImprovement: string;
  managerFeedback: string;
  status: string;
  createdAt: string;
}

export interface LeaveRecord {
  id: number;
  leaveType: string;
  startDate: string;
  endDate: string;
  totalDays: number;
  reason: string;
  status: string;
  reviewedByName?: string;
  managerRemarks?: string;
  appliedAt: string;
}