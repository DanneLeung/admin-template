<template>
  <div class="">
    <!-- Search and Action Bar -->
    <el-card class="mb-4">
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="关键字">
          <el-input v-model="queryParams.keyword" placeholder="用户名/昵称/手机号" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="部门">
          <el-tree-select v-model="queryParams.deptId" :data="deptOptions" placeholder="选择部门" clearable
            :props="{ label: 'name', value: 'id' }" />
        </el-form-item>
        <el-form-item label="公司">
          <el-select v-model="queryParams.companyId" placeholder="选择公司" clearable>
            <el-option v-for="item in companyOptions" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.enabled" placeholder="用户状态" clearable>
            <el-option label="正常" :value="true" />
            <el-option label="停用" :value="false" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
          <el-button @click="resetQuery">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
      <div class="action-buttons">
        <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增用户
          </el-button>
        <el-button type="danger" :disabled="!selectedUsers.length" @click="handleBatchDelete">
            <el-icon><Delete /></el-icon>
            批量删除
          </el-button>
      </div>
    </el-card>
    <el-card>

      <!-- User Table -->
      <el-table v-loading="loading" :data="userList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="用户名" prop="username" />
        <el-table-column label="昵称" prop="nickname" />
        <el-table-column label="部门" prop="departmentName" />
        <el-table-column label="公司" prop="companyName" />
        <el-table-column label="手机号码" prop="phone" />
        <el-table-column label="状态" align="center">
          <template #default="{ row }">
            <el-switch v-model="row.enabled"  @change="handleStatusChange(row)" />
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime"  />
        <el-table-column label="操作" width="300" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">
            <el-icon><Edit /></el-icon>
            编辑
          </el-button>
            <el-button link type="primary" @click="handleAssignRole(row)">
            <el-icon><User /></el-icon>
            分配角色
          </el-button>
            <el-button link type="danger" @click="handleDelete(row)">
            <el-icon><Delete /></el-icon>
            删除
          </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- Pagination -->
    <div class="pagination-container">
      <el-pagination v-model:current-page="queryParams.pageNum" v-model:page-size="queryParams.pageSize"
        :page-sizes="[10, 20, 50, 100]" :total="total" layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    </div>

    <!-- User Dialog -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="600px" @close="resetForm">
      <el-form ref="userFormRef" :model="userForm" :rules="userRules" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" :disabled="dialog.type === 'edit'" />
        </el-form-item>
        <el-form-item label="昵称" prop="nickname">
          <el-input v-model="userForm.nickname" />
        </el-form-item>
        <el-form-item label="公司" prop="companyId">
          <el-select v-model="userForm.companyId" placeholder="选择公司">
            <el-option v-for="item in companyOptions" :key="item.id" :label="item.name" :value="item.id"/>
          </el-select>
        </el-form-item>
        <el-form-item label="部门" prop="deptId">
          <el-tree-select v-model="userForm.deptId" :data="deptOptions" placeholder="选择部门"
            :props="{ label: 'name', value: 'id' }" />
        </el-form-item>
        <el-form-item label="手机号码" prop="phone">
          <el-input v-model="userForm.phone" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="dialog.type === 'add'">
          <el-input v-model="userForm.password" type="password" show-password />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="userForm.enabled">
            <el-radio :value="true">启用</el-radio>
            <el-radio :value="false">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialog.visible = false">
            <el-icon><Close /></el-icon>
            取消
          </el-button>
          <el-button type="primary" @click="handleSubmit">
            <el-icon><Check /></el-icon>
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- Role Assignment Dialog -->
    <el-dialog title="分配角色" v-model="roleDialog.visible" width="500px">
      <el-form :model="roleForm">
        <el-form-item label="角色列表">
          <el-checkbox-group v-model="roleForm.roleIds">
            <el-checkbox v-for="role in roleOptions" :key="role.id" :label="role.id">
              {{ role.name }}
            </el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="roleDialog.visible = false">
            <el-icon><Close /></el-icon>
            取消
          </el-button>
          <el-button type="primary" @click="handleRoleSubmit">
            <el-icon><Check /></el-icon>
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserList, createUser, updateUser, deleteUser, batchDeleteUsers, updateUserStatus, assignUserRoles, getCompanyList } from '@/api/system/user'

// Data
const loading = ref(false)
const userList = ref([])
const total = ref(0)
const selectedUsers = ref([])
const deptOptions = ref([])
const roleOptions = ref([])
const companyOptions = ref([])

// Query params
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: '',
  deptId: null,
  companyId: null,
  enabled: null
})

// Dialog control
const dialog = reactive({
  visible: false,
  title: '',
  type: 'add'
})

const roleDialog = reactive({
  visible: false,
  userId: null
})

// Form data
const userFormRef = ref(null)
const userForm = reactive({
  id: null,
  username: '',
  nickname: '',
  password: '',
  companyId: null,
  deptId: null,
  phone: '',
  email: '',
  enabled: true
})

const roleForm = reactive({
  userId: null,
  roleIds: []
})

// Form rules
const userRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  companyId: [
    { required: true, message: '请选择公司', trigger: 'change' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

// Methods
const getList = async () => {
  loading.value = true
  try {
    const res = await getUserList(queryParams)
    userList.value = res.data.content
    total.value = res.data.total
  } catch (error) {
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

// 获取公司列表
const getCompanies = async () => {
  try {
    const res = await getCompanyList()
    companyOptions.value = res.data.content
  } catch (error) {
    ElMessage.error('获取公司列表失败')
  }
}

const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

// 重置查询参数
const resetQuery = () => {
  queryParams.keyword = ''
  queryParams.deptId = null
  queryParams.companyId = null
  queryParams.enabled = null
  handleQuery()
}

const handleSelectionChange = (selection) => {
  selectedUsers.value = selection
}

const handleSizeChange = (val) => {
  queryParams.pageSize = val
  getList()
}

const handleCurrentChange = (val) => {
  queryParams.pageNum = val
  getList()
}

const handleAdd = () => {
  dialog.type = 'add'
  dialog.title = '添加用户'
  dialog.visible = true
}

const handleEdit = (row) => {
  dialog.type = 'edit'
  dialog.title = '编辑用户'
  dialog.visible = true
  Object.assign(userForm, row)
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该用户吗？', '警告', {
    type: 'warning'
  }).then(async () => {
    try {
      await deleteUser(row.id)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

const handleBatchDelete = () => {
  const ids = selectedUsers.value.map(item => item.id)
  ElMessageBox.confirm(`确认删除选中的 ${ids.length} 个用户吗？`, '警告', {
    type: 'warning'
  }).then(async () => {
    try {
      await batchDeleteUsers(ids)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

const handleStatusChange = async (row) => {
  try {
    await updateUserStatus(row.id, row.enabled)
    ElMessage.success('状态修改成功')
  } catch (error) {
    ElMessage.error('状态修改失败')
    row.enabled = !row.enabled
  }
}

const handleSubmit = async () => {
  if (!userFormRef.value) return

  try {
    await userFormRef.value.validate()
    if (dialog.type === 'add') {
      await createUser(userForm)
    } else {
      await updateUser(userForm)
    }
    ElMessage.success(dialog.type === 'add' ? '添加成功' : '修改成功')
    dialog.visible = false
    getList()
  } catch (error) {
    ElMessage.error(dialog.type === 'add' ? '添加失败' : '修改失败')
  }
}

const handleRoleSubmit = async () => {
  try {
    await assignUserRoles(roleForm)
    ElMessage.success('角色分配成功')
    roleDialog.visible = false
    getList()
  } catch (error) {
    ElMessage.error('角色分配失败')
  }
}

const resetForm = () => {
  userFormRef.value?.resetFields()
  Object.assign(userForm, {
    id: null,
    username: '',
    nickname: '',
    password: '',
    deptId: null,
    companyId: null,
    phone: '',
    email: '',
    enabled: true
  })
}

// Initialize
onMounted(() => {
  getList()
  getCompanies()
})
</script>

<style scoped>
.user-container {
  padding: 20px;
}

.action-bar {
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.search-form {
  flex: 1;
}

.action-buttons {
  margin-left: 20px;
}

.pagination-container {
  margin-top: 20px;
}
</style>