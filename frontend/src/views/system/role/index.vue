<template>
  <div class="role-container">
    <!-- Search and Action Bar -->
    <el-card class="mb-4">
      <div class="action-bar">
        <el-form :inline="true" :model="queryParams" class="search-form">
          <el-form-item label="角色名称">
            <el-input
              v-model="queryParams.name"
              placeholder="请输入角色名称"
              clearable
              @keyup.enter="handleQuery"
            />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="queryParams.status" placeholder="角色状态" clearable>
              <el-option label="正常" :value="0" />
              <el-option label="停用" :value="1" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleQuery">查询</el-button>
            <el-button @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
        <div class="action-buttons">
          <el-button type="primary" @click="handleAdd">新增角色</el-button>
          <el-button type="danger" :disabled="!selectedRoles.length" @click="handleBatchDelete">批量删除</el-button>
        </div>
      </div>
    </el-card>
    <el-card>
    <!-- Role Table -->
      <el-table
        v-loading="loading"
        :data="roleList"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="角色名称" prop="name" />
        <el-table-column label="角色编码" prop="code" />
        <el-table-column label="显示顺序" prop="sort" width="100" />
        <el-table-column label="状态" align="center" width="100">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="0"
              :inactive-value="1"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime" width="180" />
        <el-table-column label="操作"  width="300" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="primary" @click="handlePermission(row)">分配权限</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <!-- Pagination -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- Role Dialog -->
    <el-dialog
      :title="dialog.title"
      v-model="dialog.visible"
      width="500px"
      @close="resetForm"
    >
      <el-form
        ref="roleFormRef"
        :model="roleForm"
        :rules="roleRules"
        label-width="100px"
      >
        <el-form-item label="角色名称" prop="name">
          <el-input v-model="roleForm.name" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色编码" prop="code">
          <el-input v-model="roleForm.code" placeholder="请输入角色编码" />
        </el-form-item>
        <el-form-item label="显示顺序" prop="sort">
          <el-input-number v-model="roleForm.sort" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="roleForm.status">
            <el-radio :value="0">正常</el-radio>
            <el-radio :value="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="roleForm.remark" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialog.visible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- Permission Assignment Dialog -->
    <el-dialog
      title="分配权限"
      v-model="permissionDialog.visible"
      width="600px"
    >
      <el-form :model="permissionForm">
        <el-form-item>
          <el-tree
            ref="permissionTreeRef"
            :data="permissionOptions"
            :props="{ label: 'name', children: 'children' }"
            show-checkbox
            node-key="id"
            :default-checked-keys="permissionForm.permissionIds"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="permissionDialog.visible = false">取消</el-button>
          <el-button type="primary" @click="handlePermissionSubmit">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRoleList, createRole, updateRole, deleteRole, batchDeleteRoles, updateRoleStatus, assignRolePermissions } from '@/api/system/role'

// Data
const loading = ref(false)
const roleList = ref([])
const total = ref(0)
const selectedRoles = ref([])
const permissionOptions = ref([])

// Query params
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  name: '',
  status: null
})

// Dialog control
const dialog = reactive({
  visible: false,
  title: '',
  type: 'add'
})

const permissionDialog = reactive({
  visible: false,
  roleId: null
})

// Form data
const roleFormRef = ref(null)
const permissionTreeRef = ref(null)

const roleForm = reactive({
  id: null,
  name: '',
  code: '',
  sort: 0,
  status: 0,
  remark: ''
})

const permissionForm = reactive({
  roleId: null,
  permissionIds: []
})

// Form rules
const roleRules = {
  name: [
    { required: true, message: '请输入角色名称', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入角色编码', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  sort: [
    { required: true, message: '请输入显示顺序', trigger: 'blur' }
  ]
}

// Methods
const getList = async () => {
  loading.value = true
  try {
    const res = await getRoleList(queryParams)
    roleList.value = res.data.content
    total.value = res.data.total
  } catch (error) {
    ElMessage.error('获取角色列表失败')
  } finally {
    loading.value = false
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该角色吗？', '警告', {
    type: 'warning'
  }).then(async () => {
    try {
      await deleteRole(row.id)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

const handleBatchDelete = () => {
  const ids = selectedRoles.value.map(item => item.id)
  ElMessageBox.confirm(`确认删除选中的 ${ids.length} 个角色吗？`, '警告', {
    type: 'warning'
  }).then(async () => {
    try {
      await batchDeleteRoles(ids)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

const handleStatusChange = async (row) => {
  try {
    await updateRoleStatus(row.id, row.status)
    ElMessage.success('状态修改成功')
  } catch (error) {
    ElMessage.error('状态修改失败')
    row.status = row.status === 0 ? 1 : 0
  }
}

const handleSubmit = async () => {
  if (!roleFormRef.value) return

  try {
    await roleFormRef.value.validate()
    if (dialog.type === 'add') {
      await createRole(roleForm)
    } else {
      await updateRole(roleForm)
    }
    ElMessage.success(dialog.type === 'add' ? '添加成功' : '修改成功')
    dialog.visible = false
    getList()
  } catch (error) {
    ElMessage.error(dialog.type === 'add' ? '添加失败' : '修改失败')
  }
}

const handlePermissionSubmit = async () => {
  try {
    await assignRolePermissions(permissionForm)
    ElMessage.success('权限分配成功')
    permissionDialog.visible = false
    getList()
  } catch (error) {
    ElMessage.error('权限分配失败')
  }
}

const resetForm = () => {
  roleFormRef.value?.resetFields()
  Object.assign(roleForm, {
    id: null,
    name: '',
    code: '',
    sort: 0,
    status: 0,
    remark: ''
  })
}

const handleEdit = (row) => {
  dialog.type = 'edit'
  dialog.title = '编辑角色'
  dialog.visible = true
  Object.assign(roleForm, row)
}

const handlePermission = async (row) => {
  permissionDialog.visible = true
  permissionDialog.roleId = row.id
  permissionForm.roleId = row.id
  permissionForm.permissionIds = row.permissionIds || []
}

const handleSelectionChange = (selection) => {
  selectedRoles.value = selection
}

const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

const resetQuery = () => {
  queryParams.pageNum = 1
  queryParams.name = ''
  queryParams.status = null
  getList()
}

const handleAdd = () => {
  dialog.type = 'add'
  dialog.title = '新增角色'
  dialog.visible = true
  resetForm()
}

const handleSizeChange = (val) => {
  queryParams.pageSize = val
  getList()
}

const handleCurrentChange = (val) => {
  queryParams.pageNum = val
  getList()
}

// Initialize
onMounted(() => {
  getList()
  // TODO: Get permission options for tree
})
</script>

<style scoped>
.role-container {
  padding: 20px;
}

.action-bar {
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

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>