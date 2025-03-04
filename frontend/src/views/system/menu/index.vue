<template>
  <div class="menu-container">
    <!-- Action Bar -->
    <div class="action-bar">
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="菜单名称">
          <el-input
            v-model="queryParams.name"
            placeholder="请输入菜单名称"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="菜单状态" clearable>
            <el-option label="显示" :value="0" />
            <el-option label="隐藏" :value="1" />
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
        <el-button type="primary" @click="handleAdd">\n            <el-icon><Plus /></el-icon>\n            新增菜单\n          </el-button>
      </div>
    </div>

    <!-- Menu Table -->
    <el-table
      v-loading="loading"
      :data="menuList"
      row-key="id"
      :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
    >
      <el-table-column label="菜单名称" prop="name" show-overflow-tooltip>
        <template #default="{ row }">
          <span>
            <el-icon v-if="row.icon"><component :is="row.icon" /></el-icon>
            {{ row.name }}
          </span>
        </template>
      </el-table-column>
      <el-table-column label="排序" prop="sort" width="60" align="center" />
      <el-table-column label="权限标识" prop="permission" show-overflow-tooltip />
      <el-table-column label="路由地址" prop="path" show-overflow-tooltip />
      <el-table-column label="组件路径" prop="component" show-overflow-tooltip />
      <el-table-column label="类型" align="center" width="80">
        <template #default="{ row }">
          <el-tag v-if="row.type === 0" type="primary">目录</el-tag>
          <el-tag v-else-if="row.type === 1" type="success">菜单</el-tag>
          <el-tag v-else type="info">按钮</el-tag>
        </template>
      </el-table-column>
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
      <el-table-column label="操作" width="200" align="center">
        <template #default="{ row }">
          <el-button link type="primary" @click="handleAdd(row)">
            <el-icon><Plus /></el-icon>
            新增
          </el-button>
          <el-button link type="primary" @click="handleEdit(row)">
            <el-icon><Edit /></el-icon>
            编辑
          </el-button>
          <el-button link type="danger" @click="handleDelete(row)">
            <el-icon><Delete /></el-icon>
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- Menu Dialog -->
    <el-dialog
      :title="dialog.title"
      v-model="dialog.visible"
      width="600px"
      @close="resetForm"
    >
      <el-form
        ref="menuFormRef"
        :model="menuForm"
        :rules="menuRules"
        label-width="100px"
      >
        <el-form-item label="上级菜单">
          <el-tree-select
            v-model="menuForm.parentId"
            :data="menuOptions"
            :props="{ label: 'name', value: 'id' }"
            placeholder="选择上级菜单"
            clearable
          />
        </el-form-item>
        <el-form-item label="菜单类型">
          <el-radio-group v-model="menuForm.type">
            <el-radio :value="0">目录</el-radio>
            <el-radio :value="1">菜单</el-radio>
            <el-radio :value="2">按钮</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="菜单名称" prop="name">
          <el-input v-model="menuForm.name" placeholder="请输入菜单名称" />
        </el-form-item>
        <el-form-item label="图标" v-if="menuForm.type !== 2">
          <el-input v-model="menuForm.icon" placeholder="请输入图标名称" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="menuForm.sort" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="路由地址" prop="path" v-if="menuForm.type !== 2">
          <el-input v-model="menuForm.path" placeholder="请输入路由地址" />
        </el-form-item>
        <el-form-item label="组件路径" prop="component" v-if="menuForm.type === 1">
          <el-input v-model="menuForm.component" placeholder="请输入组件路径" />
        </el-form-item>
        <el-form-item label="权限标识" prop="permission" v-if="menuForm.type === 2">
          <el-input v-model="menuForm.permission" placeholder="请输入权限标识" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="menuForm.status">
            <el-radio :value="0">显示</el-radio>
            <el-radio :value="1">隐藏</el-radio>
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMenuList, createMenu, updateMenu, deleteMenu, updateMenuStatus } from '@/api/system/menu'

// Data
const loading = ref(false)
const menuList = ref([])
const menuOptions = ref([])

// Query params
const queryParams = reactive({
  name: '',
  status: null
})

// Dialog control
const dialog = reactive({
  visible: false,
  title: '',
  type: 'add'
})

// Form data
const menuFormRef = ref(null)
const menuForm = reactive({
  id: null,
  parentId: null,
  name: '',
  icon: '',
  type: 0,
  sort: 0,
  path: '',
  component: '',
  permission: '',
  status: 0
})

// Form rules
const menuRules = {
  name: [
    { required: true, message: '请输入菜单名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  path: [
    { required: true, message: '请输入路由地址', trigger: 'blur' }
  ],
  component: [
    { required: true, message: '请输入组件路径', trigger: 'blur' }
  ],
  permission: [
    { required: true, message: '请输入权限标识', trigger: 'blur' }
  ]
}

// Methods
const getList = async () => {
  loading.value = true
  try {
    const res = await getMenuList(queryParams)
    menuList.value = res.data.content
  } catch (error) {
    ElMessage.error('获取菜单列表失败')
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
  queryParams.status = null
  handleQuery()
}

const handleAdd = (row) => {
  dialog.type = 'add'
  dialog.title = '添加菜单'
  dialog.visible = true
  if (row) {
    menuForm.parentId = row.id
  }
}

const handleEdit = (row) => {
  dialog.type = 'edit'
  dialog.title = '编辑菜单'
  dialog.visible = true
  Object.assign(menuForm, row)
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该菜单吗？', '警告', {
    type: 'warning'
  }).then(async () => {
    try {
      await deleteMenu(row.id)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

const handleStatusChange = async (row) => {
  try {
    await updateMenuStatus(row.id, row.status)
    ElMessage.success('状态修改成功')
  } catch (error) {
    ElMessage.error('状态修改失败')
    row.status = row.status === 0 ? 1 : 0
  }
}

const handleSubmit = async () => {
  if (!menuFormRef.value) return

  try {
    await menuFormRef.value.validate()
    if (dialog.type === 'add') {
      await createMenu(menuForm)
    } else {
      await updateMenu(menuForm)
    }
    ElMessage.success(dialog.type === 'add' ? '添加成功' : '修改成功')
    dialog.visible = false
    getList()
  } catch (error) {
    ElMessage.error(dialog.type === 'add' ? '添加失败' : '修改失败')
  }
}

const resetForm = () => {
  menuFormRef.value?.resetFields()
  Object.assign(menuForm, {
    id: null,
    parentId: null,
    name: '',
    icon: '',
    type: 0,
    sort: 0,
    path: '',
    component: '',
    permission: '',
    status: 0
  })
}

// Initialize
onMounted(() => {
  getList()
  // TODO: Get menu options for tree-select
})
</script>

<style scoped>
.menu-container {
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

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>