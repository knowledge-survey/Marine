export const ROLES = {
  BREEDER: 'BREEDER',
  MANAGER: 'MANAGER',
  OPERATOR: 'OPERATOR',
  SUPER_ADMIN: 'SUPER_ADMIN'
}

export const hasRole = (userRole, requiredRoles) => {
  if (!userRole) return false
  if (Array.isArray(requiredRoles)) {
    return requiredRoles.includes(userRole)
  }
  return userRole === requiredRoles
}

export const getRoleLevel = (role) => {
  switch (role) {
    case ROLES.SUPER_ADMIN: return 4
    case ROLES.OPERATOR: return 3
    case ROLES.MANAGER: return 2
    case ROLES.BREEDER: return 1
    default: return 0
  }
}

export const isBreeder = (role) => role === ROLES.BREEDER
export const isManager = (role) => role === ROLES.MANAGER
export const isOperator = (role) => role === ROLES.OPERATOR
export const isSuperAdmin = (role) => role === ROLES.SUPER_ADMIN

export const isManagerOrAbove = (role) => [ROLES.MANAGER, ROLES.OPERATOR, ROLES.SUPER_ADMIN].includes(role)
export const isOperatorOrAbove = (role) => [ROLES.OPERATOR, ROLES.SUPER_ADMIN].includes(role)

export const getRoleLabel = (role) => {
  switch (role) {
    case ROLES.SUPER_ADMIN: return '超级管理员'
    case ROLES.OPERATOR: return '系统运维人员'
    case ROLES.MANAGER: return '管理人员'
    case ROLES.BREEDER: return '养殖人员'
    default: return '未知角色'
  }
}

export const getRoleClass = (role) => {
  switch (role) {
    case ROLES.SUPER_ADMIN: return 'role-super'
    case ROLES.OPERATOR: return 'role-operator'
    case ROLES.MANAGER: return 'role-manager'
    case ROLES.BREEDER: return 'role-breeder'
    default: return ''
  }
}