<template>
  <div class="">
    <el-row :gutter="20">
      <!-- 左侧部门树 -->
      <el-col :span="8" :xs="24">
        <div class="head-container">
          <el-input v-model="deptName" placeholder="请输入部门名称" clearable prefix-icon="Search"
            style="margin-bottom: 20px" />
          <el-button type="primary" @click="handleAdd" v-hasPermi="['system:department:add']"
            style="width: 100%; margin-bottom: 20px">
            <el-icon>
              <Plus />
            </el-icon>
            新增根节点
          </el-button>
        </div>
        <div class="head-container">
          <el-table :data="deptOptions" row-key="id" :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
            style="width: 100%">
            <el-table-column prop="name" label="部门名称" />
            <el-table-column prop="sort" label="排序" width="80" />
            <el-table-column label="状态" width="80" align="center">
              <template #default="{ row }">
                <dict-tag :options="deptStatusOptions" :value="row.status" />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="180" align="center">
              <template #default="{ row }">
                <el-button link type="primary" @click="handleNodeClick(row)">
                  <el-icon>
                    <View />
                  </el-icon>
                  查看
                </el-button>
                <el-button link type="primary" @click="handleAddChild(row)" v-hasPermi="['system:department:add']">
                  <el-icon>
                    <Plus />
                  </el-icon>
                  新增子部门
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-col>
      <!-- 右侧部门详情/编辑区域 -->
      <el-col :span="16" :xs="24">
        <el-card v-if="dept.id">
          <template #header>
            <div class="card-header">
              <span>部门信息</span>
              <div class="right-menu">
                <el-button type="primary" @click="handleEdit" v-hasPermi="['system:department:edit']">
                  <el-icon>
                    <Edit />
                  </el-icon>
                  编辑
                </el-button>
                <el-button type="danger" @click="handleDelete" v-hasPermi="['system:department:delete']">
                  <el-icon>
                    <Delete />
                  </el-icon>
                  删除
                </el-button>
              </div>
            </div>
          </template>
          <el-descriptions :column="2">
            <el-descriptions-item label="部门名称">{{ dept.name }}</el-descriptions-item>
            <el-descriptions-item label="显示排序">{{ dept.sort }}</el-descriptions-item>
            <el-descriptions-item label="负责人">{{ dept.leader }}</el-descriptions-item>
            <el-descriptions-item label="联系电话">{{ dept.phone }}</el-descriptions-item>
            <el-descriptions-item label="邮箱">{{ dept.email }}</el-descriptions-item>
            <el-descriptions-item label="部门状态">
              <dict-tag :options="deptStatusOptions" :value="dept.status" />
            </el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ dept.createTime }}</el-descriptions-item>
          </el-descriptions>
        </el-card>

        <!-- 添加/修改部门对话框 -->
        <el-dialog :title="title" v-model="open" width="600px" append-to-body>
          <el-form ref="deptForm" :model="form" :rules="rules" label-width="80px">
            <el-form-item label="上级部门">
              <el-tree-select v-model="form.parentId" :data="deptOptions"
                :props="{ label: 'name', children: 'children', value: 'id' }" value-key="id" placeholder="选择上级部门"
                check-strictly />
            </el-form-item>
            <el-form-item label="部门名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入部门名称" />
            </el-form-item>
            <el-form-item label="显示排序" prop="sort">
              <el-input-number v-model="form.sort" :min="0" />
            </el-form-item>
            <el-form-item label="负责人" prop="leader">
              <el-input v-model="form.leader" placeholder="请输入负责人" maxlength="20" />
            </el-form-item>
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入联系电话" maxlength="11" />
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" maxlength="50" />
            </el-form-item>
            <el-form-item label="部门状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio v-for="dict in deptStatusOptions" :key="dict.value" :label="dict.value">
                  {{ dict.label }}
                </el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="所属公司" prop="companyId">
              <el-select v-model="form.companyId" placeholder="请选择所属公司">
                <el-option v-for="item in companyOptions" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-form>
          <template #footer>
            <div class="dialog-footer">
              <el-button type="primary" @click="submitForm">
                <el-icon>
                  <Check />
                </el-icon>
                确 定
              </el-button>
              <el-button @click="cancel">
                <el-icon>
                  <Close />
                </el-icon>
                取 消
              </el-button>
            </div>
          </template>
        </el-dialog>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getDepartment, listDepartments, addDepartment, updateDepartment, delDepartment } from '@/api/system/department'
import { listCompany } from '@/api/system/company'

// 部门树选项
const deptOptions = ref([])
// 选中的部门信息
const dept = ref({})
// 部门名称（搜索用）
const deptName = ref('')
// 是否显示弹出层
const open = ref(false)
// 弹出层标题
const title = ref('')
// 表单参数
const form = ref({
  id: undefined,
  parentId: undefined,
  name: undefined,
  sort: 0,
  leader: undefined,
  phone: undefined,
  email: undefined,
  status: 0,
  companyId: undefined
})

// 公司选项列表
const companyOptions = ref([])

// 表单校验规则
const rules = {
  name: [
    { required: true, message: '部门名称不能为空', trigger: 'blur' }
  ],
  sort: [
    { required: true, message: '显示排序不能为空', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
}

// 部门状态数据字典
const deptStatusOptions = [
  { label: '正常', value: 0 },
  { label: '停用', value: 1 }
]

// 树形控件
const deptTree = ref(null)

// 监听部门名称变化
watch(deptName, (val) => {
  deptTree.value?.filter(val)
})

// 加载部门数据
const loadDeptData = async (node, resolve) => {
  if (node.level === 0) {
    const response = await listDepartments()
    resolve(response.data || [])
  } else {
    resolve(node.data.children || [])
  }
}

// 树节点过滤
const filterNode = (value, data) => {
  if (!value) return true
  return data.name.includes(value)
}

// 节点单击事件
const handleNodeClick = async (data) => {
  const response = await getDepartment(data.id)
  dept.value = response.data
}

// 在 script setup 部分添加 handleAddChild 方法
const handleAddChild = (row) => {
  reset()
  form.value.parentId = row.id
  open.value = true
  title.value = '新增子部门'
}

// 修改 reset 方法，添加 companyId 的重置
const reset = () => {
  form.value = {
    id: undefined,
    parentId: undefined,
    name: undefined,
    sort: 0,
    leader: undefined,
    phone: undefined,
    email: undefined,
    status: 0,
    companyId: undefined
  }
}

// 取消按钮
const cancel = () => {
  open.value = false
  reset()
}

// 显示修改弹出层
const handleEdit = () => {
  form.value = { ...dept.value }
  open.value = true
  title.value = '修改部门'
}

// 显示新增弹出层
const handleAdd = () => {
  reset()
  open.value = true
  title.value = '新增部门'
}

// 删除按钮操作
const handleDelete = async () => {
  await ElMessageBox.confirm('是否确认删除该部门？', '警告', {
    type: 'warning'
  })

  try {
    await delDepartment(dept.value.id)
    ElMessage.success('删除成功')
    // 刷新部门树
    loadDeptData({ level: 0 }, (data) => {
      deptOptions.value = data.content
    })
    dept.value = {}
  } catch (error) {
    ElMessage.error('删除失败：' + error.message)
  }
}

// 提交表单
const submitForm = async () => {
  try {
    if (form.value.id) {
      await updateDepartment(form.value.id, form.value)
      ElMessage.success('修改成功')
    } else {
      await addDepartment(form.value)
      ElMessage.success('新增成功')
    }
    open.value = false
    // 刷新部门树
    loadDeptData({ level: 0 }, (data) => {
      deptOptions.value = data
    })
  } catch (error) {
    ElMessage.error('操作失败：' + error.message)
  }
}

// 页面加载时获取部门树数据
onMounted(async () => {
  loadDeptData({ level: 0 }, (data) => {
    deptOptions.value = data
  })

  // 加载公司列表
  try {
    const response = await listCompany()
    companyOptions.value = response.data.content
  } catch (error) {
    ElMessage.error('获取公司列表失败：' + error.message)
  }
})
</script>

<style scoped>
.app-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.right-menu {
  display: flex;
  gap: 10px;
}

.dialog-footer {
  text-align: right;
}
</style>