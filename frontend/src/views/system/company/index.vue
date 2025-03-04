<template>
  <div>
   <el-card class="mb-4">
      <!-- 搜索表单 -->
      <el-form :model="queryParams" inline class="mb-4">
        <el-form-item label="公司名称">
          <el-input v-model="queryParams.name" placeholder="请输入公司名称" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.enabled" placeholder="请选择状态" clearable>
            <el-option label="启用" :value="true" />
            <el-option label="禁用" :value="false" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 操作按钮 -->
      <div class="action-buttons">
        <el-button type="primary" @click="handleAdd" v-hasPermi="['company:add']">新增</el-button>
      </div>
    </el-card>
    <el-card class="mb-4">
      <!-- 数据表格 -->
      <el-table v-loading="loading" :data="companyList">
        <el-table-column label="公司名称" prop="name" />
        <el-table-column label="公司编码" prop="code" />
        <el-table-column label="域名" prop="domain" />
        <el-table-column label="状态" align="center">
          <template #default="{ row }">
            <el-tag :type="row.enabled ? 'success' : 'danger'">
              {{ row.enabled ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" prop="createTime" width="180" />
        <el-table-column label="操作" align="center" width="200">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)" v-hasPermi="['company:edit']">编辑</el-button>
            <el-button link type="primary" @click="handleDelete(row)" v-hasPermi="['company:delete']">删除</el-button>
            <el-button
              link
              type="primary"
              @click="handleStatus(row)"
              v-hasPermi="['company:edit']"
            >{{ row.enabled ? '禁用' : '启用'  }}</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <!-- 分页组件 -->
    <div class="mt-4 flex justify-end">
      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        :page-sizes="[10, 20, 30, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 表单弹窗 -->
    <el-dialog
      :title="dialog.title"
      v-model="dialog.visible"
      width="500px"
      append-to-body
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item label="公司名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入公司名称" />
        </el-form-item>
        <el-form-item label="公司编码" prop="code">
          <el-input v-model="form.code" placeholder="请输入公司编码" />
        </el-form-item>
        <el-form-item label="域名" prop="domain">
          <el-input v-model="form.domain" placeholder="请输入域名" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.enabled">
            <el-radio :label="true">启用</el-radio>
            <el-radio :label="false">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="dialog.visible = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listCompany, getCompany, addCompany, updateCompany, delCompany, changeCompanyStatus } from '@/api/system/company'

// 查询参数
const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  name: undefined,
  enabled: undefined
})

// 数据列表
const companyList = ref([])
const total = ref(0)
const loading = ref(false)

// 弹窗控制
const dialog = reactive({
  title: '',
  visible: false
})

// 表单对象
const formRef = ref()
const form = reactive({
  id: undefined,
  name: '',
  code: '',
  domain: '',
  enabled: true
})

// 表单校验规则
const rules = {
  name: [
    { required: true, message: '请输入公司名称', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入公司编码', trigger: 'blur' }
  ]
}

// 查询列表
const getList = async () => {
  loading.value = true
  try {
    const { data } = await listCompany(queryParams)
    companyList.value = data.content
    total.value = data.total
  } finally {
    loading.value = false
  }
}

// 查询按钮操作
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

// 重置按钮操作
const resetQuery = () => {
  queryParams.name = undefined
  queryParams.enabled = undefined
  handleQuery()
}

// 新增按钮操作
const handleAdd = () => {
  dialog.title = '添加公司'
  dialog.visible = true
  Object.assign(form, {
    id: undefined,
    name: '',
    code: '',
    domain: '',
    enabled: true
  })
}

// 编辑按钮操作
const handleEdit = async (row) => {
  dialog.title = '编辑公司'
  dialog.visible = true
  const { data } = await getCompany(row.id)
  Object.assign(form, data)
}

// 提交表单
const submitForm = async () => {
  await formRef.value.validate()
  
  if (form.id) {
    await updateCompany(form.id, form)
  } else {
    await addCompany(form)
  }
  
  ElMessage.success('操作成功')
  dialog.visible = false
  getList()
}

// 删除按钮操作
const handleDelete = (row) => {
  ElMessageBox.confirm('确认要删除该公司吗？', '警告', {
    type: 'warning'
  }).then(async () => {
    await delCompany(row.id)
    ElMessage.success('删除成功')
    getList()
  })
}

// 状态修改
const handleStatus = async (row) => {
  const text = row.enabled ? '禁用' : '启用'
  try {
    await ElMessageBox.confirm(`确认要${text}该公司吗？`, '警告', {
      type: 'warning'
    })
    await changeCompanyStatus(row.id, !row.enabled)
    ElMessage.success(`${text}成功`)
    getList()
  } catch (error) {
    console.error(error)
  }
}

// 分页大小改变
const handleSizeChange = (val) => {
  queryParams.pageSize = val
  getList()
}

// 页码改变
const handleCurrentChange = (val) => {
  queryParams.pageNum = val
  getList()
}

// 弹窗关闭前的回调
const handleDialogClose = () => {
  formRef.value?.resetFields()
}

// 初始化
onMounted(() => {
  getList()
})
</script>