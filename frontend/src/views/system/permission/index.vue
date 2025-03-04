<template>
  <div class="">
    <!-- Search and Filter Section -->
    <el-card class="filter-container">
      <el-form :inline="true" :model="queryParams" @submit.prevent>
        <el-form-item label="权限名称">
          <el-input
            v-model="queryParams.name"
            placeholder="请输入权限名称"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="权限标识">
          <el-input
            v-model="queryParams.code"
            placeholder="请输入权限标识"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="权限状态" clearable>
            <el-option label="启用" :value="0" />
            <el-option label="禁用" :value="1" />
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
    </el-card>

    <!-- Table Section -->
    <el-card class="table-container">
      <template #header>
        <div class="card-header">
          <span>权限列表</span>
          <el-button
            type="primary"
            @click="handleAdd"
            v-if="hasPermission('system:permission:add')"
          >
            <el-icon><Plus /></el-icon>
            新增
          </el-button>
          <el-button
            type="danger"
            @click="handleBatchDelete"
            v-if="hasPermission('system:permission:delete')"
            :disabled="!selectedPermissions.length"
          >
            <el-icon><Delete /></el-icon>
            批量删除
          </el-button>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="permissionList"
        border
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column type="index" label="序号" width="50" />
        <el-table-column prop="name" label="权限名称" />
        <el-table-column prop="code" label="权限标识" />
        <el-table-column prop="description" label="权限描述" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'danger'">
              {{ row.status === 0 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              link
              @click="handleEdit(row)"
              v-if="hasPermission('system:permission:edit')"
            >
            <el-icon><Edit /></el-icon>
            编辑
          </el-button>
          <el-button
              type="danger"
              link
              @click="handleDelete(row)"
              v-if="hasPermission('system:permission:delete')"
            >
            <el-icon><Delete /></el-icon>
            删除
          </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        :page-sizes="[10, 20, 30, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>

    <!-- Dialog Form -->
    <el-dialog
      :title="dialog.title"
      v-model="dialog.visible"
      width="500px"
      append-to-body
    >
      <el-form
        ref="permissionFormRef"
        :model="permissionForm"
        :rules="permissionRules"
        label-width="100px"
      >
        <el-form-item label="权限名称" prop="name">
          <el-input v-model="permissionForm.name" placeholder="请输入权限名称" />
        </el-form-item>
        <el-form-item label="权限标识" prop="code">
          <el-input v-model="permissionForm.code" placeholder="请输入权限标识" />
        </el-form-item>
        <el-form-item label="权限描述" prop="description">
          <el-input
            v-model="permissionForm.description"
            type="textarea"
            placeholder="请输入权限描述"
          />
        </el-form-item>
        <el-form-item label="权限状态" prop="status">
          <el-radio-group v-model="permissionForm.status">
            <el-radio :value="0">启用</el-radio>
            <el-radio :value="1">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialog.visible = false">
            <el-icon><Close /></el-icon>
            取 消
          </el-button>
          <el-button type="primary" @click="submitForm">
            <el-icon><Check /></el-icon>
            确 定
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/user'
import { getPermissionList, createPermission, updatePermission, deletePermission, batchDeletePermissions, updatePermissionStatus } from '@/api/system/permission'

// Store
const userStore = useUserStore()
const hasPermission = userStore.hasPermission

// Data
const loading = ref(false)
const permissionList = ref([])
const total = ref(0)
const selectedPermissions = ref([])

// Query params
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  name: '',
  code: '',
  status: null
})

// Dialog control
const dialog = reactive({
  visible: false,
  title: '',
  type: 'add'
})

// Form data
const permissionFormRef = ref(null)
const permissionForm = reactive({
  id: null,
  name: '',
  code: '',
  description: '',
  status: 0
})

// Form rules
const permissionRules = {
  name: [
    { required: true, message: '请输入权限名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入权限标识', trigger: 'blur' }
  ]
}

// Methods
const getList = async () => {
  loading.value = true
  try {
    const res = await getPermissionList(queryParams)
    permissionList.value = res.data.content
    total.value = res.data.total
  } catch (error) {
    ElMessage.error('获取权限列表失败')
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

const resetQuery = () => {
  queryParams.name = ''
  queryParams.code = ''
  queryParams.status = null
  handleQuery()
}

const handleSubmit = async () => {
  if (!permissionFormRef.value) return

  try {
    await permissionFormRef.value.validate()
    if (dialog.type === 'add') {
      await createPermission(permissionForm)
    } else {
      await updatePermission(permissionForm)
    }
    ElMessage.success(dialog.type === 'add' ? '添加成功' : '修改成功')
    dialog.visible = false
    getList()
  } catch (error) {
    ElMessage.error(dialog.type === 'add' ? '添加失败' : '修改失败')
  }
}

const handleSizeChange = (val) => {
  queryParams.pageSize = val
  getList()
}

const handleCurrentChange = (val) => {
  queryParams.pageNum = val
  getList()
}

const resetForm = () => {
  permissionForm.id = null
  permissionForm.name = ''
  permissionForm.code = ''
  permissionForm.description = ''
  permissionForm.status = 0
}

const handleAdd = () => {
  resetForm()
  dialog.type = 'add'
  dialog.title = '添加权限'
  dialog.visible = true
}

const handleEdit = (row) => {
  dialog.type = 'edit'
  dialog.title = '编辑权限'
  dialog.visible = true
  Object.assign(permissionForm, row)
}

const submitForm = async () => {
  if (!permissionFormRef.value) return

  await permissionFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // TODO: Implement API call
        // const api = dialog.type === 'add' ? addPermission : updatePermission
        // await api(permissionForm)
        ElMessage.success(`${dialog.type === 'add' ? '添加' : '修改'}成功`)
        dialog.visible = false
        getList()
      } catch (error) {
        ElMessage.error(`${dialog.type === 'add' ? '添加' : '修改'}失败`)
      }
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`是否确认删除名称为"${row.name}"的权限?`, '警告', {
    type: 'warning'
  }).then(async () => {
    try {
      // TODO: Implement API call
      // await deletePermission(row.id)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

const handleBatchDelete = () => {
  ElMessageBox.confirm('是否确认删除选中的权限?', '警告', {
    type: 'warning'
  }).then(async () => {
    try {
      // TODO: Implement API call
      // await batchDeletePermissions(selectedPermissions.value.map(item => item.id))
      ElMessage.success('批量删除成功')
      getList()
    } catch (error) {
      ElMessage.error('批量删除失败')
    }
  })
}

const handleSelectionChange = (selection) => {
  selectedPermissions.value = selection
}

onMounted(() => {
  getList()
})
</script>

<style scoped>

.filter-container {
  margin-bottom: 20px;
}

.table-container {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.dialog-footer {
  text-align: right;
}
</style>