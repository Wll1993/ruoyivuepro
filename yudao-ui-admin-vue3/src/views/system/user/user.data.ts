import { reactive } from 'vue'
import { required } from '@/utils/formRules'
import { useI18n } from '@/hooks/web/useI18n'
import { DICT_TYPE } from '@/utils/dict'
import { VxeCrudSchema, useVxeCrudSchemas } from '@/hooks/web/useVxeCrudSchemas'
// 国际化
const { t } = useI18n()
// 表单校验
export const rules = reactive({
  username: [required],
  nickname: [required],
  email: [required],
  status: [required],
  mobile: [
    {
      pattern:
        /^(?:(?:\+|00)86)?1(?:(?:3[\d])|(?:4[5-79])|(?:5[0-35-9])|(?:6[5-7])|(?:7[0-8])|(?:8[\d])|(?:9[189]))\d{8}$/, // TODO @星语：前端只校验长度，格式交给后端；因为号码格式不断在变的
      trigger: 'blur',
      message: '请输入正确的手机号码'
    }
  ]
})
// crudSchemas
const crudSchemas = reactive<VxeCrudSchema>({
  primaryKey: 'id',
  primaryType: 'seq',
  primaryTitle: '用户编号',
  action: true,
  actionWidth: '400px',
  columns: [
    {
      title: '用户账号',
      field: 'username',
      isSearch: true
    },
    {
      title: '用户密码',
      field: 'password',
      isDetail: false,
      isTable: false,
      form: {
        component: 'InputPassword'
      }
    },
    {
      title: '用户昵称',
      field: 'nickname'
    },
    {
      title: '用户邮箱',
      field: 'email'
    },
    {
      title: '手机号码',
      field: 'mobile',
      isSearch: true
    },
    {
      title: '部门',
      field: 'deptId', // TODO 星语：详情的部门没展示
      isTable: false
    },
    {
      title: '岗位',
      field: 'postIds', // TODO 星语：岗位为空的时候，要不要不展示
      isTable: false
    },
    {
      title: t('common.status'),
      field: 'status',
      dictType: DICT_TYPE.COMMON_STATUS,
      dictClass: 'number',
      isSearch: true
    },
    {
      title: '最后登录时间',
      field: 'loginDate',
      formatter: 'formatDate', // TODO 星语：未登录的时候，不要展示 Invalid Date
      isForm: false
    },
    {
      title: '最后登录IP',
      field: 'loginIp',
      isTable: false,
      isForm: false
    },
    {
      title: t('form.remark'),
      field: 'remark',
      isTable: false
    },
    {
      title: t('common.createTime'),
      field: 'createTime',
      formatter: 'formatDate',
      isTable: false,
      isForm: false,
      search: {
        show: true,
        itemRender: {
          name: 'XDataTimePicker'
        }
      }
    }
  ]
})
export const { allSchemas } = useVxeCrudSchemas(crudSchemas)
