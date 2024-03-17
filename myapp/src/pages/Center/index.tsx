import { currentUser, modify } from '@/services/ant-design-pro/api';
import type { ProFormInstance } from '@ant-design/pro-components';
import {
  ProForm,
  ProFormSelect,
  ProFormText,
} from '@ant-design/pro-components';
import { message } from 'antd';
import { useRef } from 'react';
import { useModel,history } from '@umijs/max';
import { SUCCESS_CODE } from '@/constant';

export default () => {
  const { initialState, setInitialState } = useModel('@@initialState');

  if(initialState?.currentUser === null){
    history.replace('/user/login');
  }

  const formRef = useRef<
    ProFormInstance<{
      userAccount: string;
      userName: string;
      phone: string;
      email: string;
      avatarUrl: string;
      gender: number;
    }>
  >();
  return (
    <ProForm<{
      userAccount: string;
      userName: string;
      phone: string;
      email: string;
      avatarUrl: string;
      gender: number;
    }>
      onFinish={async (values) => {
        console.log(values);
        const val1 = await formRef.current?.validateFields();
        console.log('validateFields:', val1);
        const val2 = await formRef.current?.validateFieldsReturnFormatValue?.();
        console.log('validateFieldsReturnFormatValue:', val2);
        // 用户账户: 4-10位字母、数字、下划线，不要求全包含
        const userAccountRegExp: RegExp = /^\w{4,10}$/;
        if(val2?.userAccount != null && !userAccountRegExp.test(val2?.userAccount)){
          message.error('userAccount格式不正确,4-10位字母、数字或下划线');
          return;
        }
        // 电话格式：1开头，11位数字
        const phoneRegExp: RegExp = /^1\d{10}$/;
        if(val2?.phone != null && val2?.phone != "" && !phoneRegExp.test(val2.phone)){
          message.error('phone格式不正确');
          return;          
        }
        // 邮箱格式："^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
        const emailRegExp: RegExp = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;
        if(val2?.email != null && val2?.email != "" && !emailRegExp.test(val2?.email)){
          message.error('email格式不正确');
          return;          
        }
        // 头像url格式："^https?://[\\w.-]+(:\\d+)?(/[\\w./_-]+)*\\.(png|jpg|jpeg|gif|bmp|webp|svg)$"
        const avatarUrlRegExp: RegExp = /^https?:\/\/[\w.-]+(:\d+)?(\/[\w./_-]+)*\.(png|jpg|jpeg|gif|bmp|webp|svg)$/;
        if(val2?.avatarUrl != null && val2?.avatarUrl != "" && !avatarUrlRegExp.test(val2?.avatarUrl)){
          message.error('avatarUrl格式不正确');
          return;          
        }
        const res = await modify({
          userAccount: val2?.userAccount,
          userName: val2?.userName,
          phone: val2?.phone,
          email: val2?.email,
          avatarUrl: val2?.avatarUrl,
          gender: val2?.gender
        });
        if(res === true){
          message.success('个人信息修改成功');
          history.push('/welcome');
        }
        else{
          message.error('个人信息修改失败');
        }
      }}
      formRef={formRef}
      params={{ id: '100' }}
      formKey="base-form-use-demo"
      // dateFormatter={(value, valueType) => {
      //   console.log('---->', value, valueType);
      //   return value.format('YYYY/MM/DD HH:mm:ss');
      // }}
      // request={async () => {
      //   return {
      //     userAccount: "",
      //     userName: "",
      //     phone: "",
      //     email: "",
      //     avatarUrl: "",
      //   };
      // }}
      autoFocusFirstInput
    >
      
      <ProForm.Group>
        <ProFormText
          width="md"
          name="userAccount"
          required
          dependencies={[['contract', 'userAccount']]}
          label="userAccount"
          placeholder="请输入UserAccount"
          initialValue={initialState?.currentUser?.userAccount}
          rules={[{ required: true, message: '这是必填项' }]}
        />
        <ProFormText
          width="md"
          name="username"
          label="username"
          placeholder="请输入username"
          initialValue={initialState?.currentUser?.username}
        />
        <ProFormText
          width="md"
          name="phone"
          label="phone"
          placeholder="请输入phone"
          initialValue={initialState?.currentUser?.phone}
        />
        <ProFormText
          width="md"
          name="email"
          label="email"
          placeholder="请输入email"
          initialValue={initialState?.currentUser?.email}
        />
        <ProFormText
          width="md"
          name="avatarUrl"
          label="avatarUrl"
          placeholder="请输入avatarUrl"
          initialValue={initialState?.currentUser?.avatarUrl}
        />
      </ProForm.Group>
      <ProForm.Group>
      <ProFormSelect.SearchSelect
          options={[
            {
              value: 'gender',
              type: Number,
              options: [
                {
                  value: 0,
                  label: '男',
                },
                {
                  value: 1,
                  label: '女',
                },
              ],
            },
          ]}
          width="xs"
          cacheForSwr
          name="gender"
          label="gender"
          placeholder={'请选择gender'}
          initialValue={initialState?.currentUser?.gender}
        />
      </ProForm.Group>
      
    </ProForm>
  );
};