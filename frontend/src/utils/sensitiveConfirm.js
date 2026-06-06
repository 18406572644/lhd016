import Vue from 'vue'

export function sensitiveConfirm(options) {
  const {
    title = '敏感操作确认',
    message = '您正在执行敏感操作，请输入操作原因：',
    confirmButtonText = '确认执行',
    cancelButtonText = '取消',
    type = 'warning',
    inputPlaceholder = '请输入操作原因（至少5个字符）',
    minLength = 5
  } = options

  return new Promise((resolve, reject) => {
    let reason = ''

    const MessageBox = Vue.extend({
      data() {
        return {
          visible: true,
          confirmLoading: false,
          inputReason: ''
        }
      },
      render(h) {
        return h('el-dialog', {
          props: {
            title,
            visible: this.visible,
            width: '480px',
            closeOnClickModal: false,
            closeOnPressEscape: false
          },
          on: {
            updateVisible: (val) => {
              this.visible = val
            }
          }
        }, [
          h('div', {
            style: { padding: '10px 20px 20px' }
          }, [
            h('div', {
              style: {
                display: 'flex',
                alignItems: 'flex-start',
                marginBottom: '16px'
              }
            }, [
              h('i', {
                class: `el-icon-${type === 'warning' ? 'warning' : type === 'danger' ? 'error' : 'info'}`,
                style: {
                  fontSize: '24px',
                  marginRight: '12px',
                  color: type === 'warning' ? '#E6A23C' : type === 'danger' ? '#F56C6C' : '#409EFF'
                }
              }),
              h('div', {
                style: {
                  flex: 1,
                  fontSize: '14px',
                  color: '#606266',
                  lineHeight: '1.6',
                  paddingTop: '4px'
                }
              }, message)
            ]),
            h('el-input', {
              props: {
                type: 'textarea',
                value: this.inputReason,
                placeholder: inputPlaceholder,
                rows: 3,
                maxlength: 200,
                showWordLimit: true
              },
              on: {
                input: (val) => {
                  this.inputReason = val
                }
              },
              ref: 'reasonInput'
            }),
            h('div', {
              style: {
                marginTop: '20px',
                textAlign: 'right'
              }
            }, [
              h('el-button', {
                props: {
                  size: 'medium'
                },
                on: {
                  click: () => {
                    this.visible = false
                    reject(new Error('cancel'))
                    setTimeout(() => this.$destroy(), 300)
                  }
                }
              }, cancelButtonText),
              h('el-button', {
                props: {
                  type: 'primary',
                  size: 'medium',
                  loading: this.confirmLoading
                },
                on: {
                  click: async () => {
                    reason = this.inputReason.trim()
                    if (!reason || reason.length < minLength) {
                      this.$message.warning(`请输入操作原因，至少${minLength}个字符`)
                      return
                    }
                    this.confirmLoading = true
                    try {
                      this.visible = false
                      resolve(reason)
                      setTimeout(() => this.$destroy(), 300)
                    } catch (error) {
                      this.confirmLoading = false
                      this.$message.error(error.message || '操作失败')
                    }
                  }
                }
              }, confirmButtonText)
            ])
          ])
        ])
      },
      mounted() {
        this.$nextTick(() => {
          const input = this.$refs.reasonInput
          if (input && input.$el) {
            const textarea = input.$el.querySelector('textarea')
            if (textarea) {
              textarea.focus()
            }
          }
        })
      }
    })

    const instance = new MessageBox()
    instance.$mount()
    document.body.appendChild(instance.$el)
  })
}

export default {
  install(Vue) {
    Vue.prototype.$sensitiveConfirm = sensitiveConfirm
  }
}
