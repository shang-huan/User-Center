import { SUCCESS_CODE } from '@/constant';
import { search } from '@/services/ant-design-pro/api';
import { EllipsisOutlined, PlusOutlined } from '@ant-design/icons';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import { ProTable, TableDropdown } from '@ant-design/pro-components';
import { Button, Dropdown, Space, Tag, message } from 'antd';
import { useRef } from 'react';
import request from 'umi-request';

const columns: ProColumns<API.CurrentUser>[] = [
    // id?: number;
    // userAccount?: string;
    // avatarUrl?: string;
    // phone?: string;
    // email?: string;
    // username?: string;
    // gender?: number;
    // userRole?: number;
    {
    dataIndex: 'id',
    valueType: 'indexBorder',
    width: 48,
    },
    {
        title: 'userAccount',
        dataIndex: 'userAccount',
        valueType: 'text',
        ellipsis: true,
    },
    {
        title: 'Avatar',
        dataIndex: 'avatarUrl',
        valueType: 'avatar',
        copyable: false,
    },
    {
    title: 'Username',
    dataIndex: 'username',
    valueType: 'text',
    ellipsis: true,
    },
    {
    title: 'Phone',
    dataIndex: 'phone',
    valueType: 'text',
    ellipsis: true,
    },
    {
    title: 'Email',
    dataIndex: 'email',
    valueType: 'text',
    ellipsis: true,
    },
    {
        title: 'Gender',
        dataIndex: 'gender',
        valueType: 'select',
        valueEnum: {
            0: {
                text: '男',
            },
            1: {
                text: '女',
            }
        }
    },
    {
        title: 'UserRole',
        dataIndex: 'userRole',
        valueType: 'select',
        valueEnum: {
            1: {
                text: '管理员',
                status: 'Success'
            },
            0: {
                text: '用户',
                disabled: true,
                status : 'Default'
            }
        }
    }
];

export default () => {
  const actionRef = useRef<ActionType>();
  return (
    <ProTable<API.CurrentUser>
      columns={columns}
      actionRef={actionRef}
      cardBordered
      request = {async (params, sort, filter) => {
        console.log(sort, filter);
        const result : API.SearchResult = await search({"userAccount":""});
        return {
            data: result
          }
      }}
      editable={{
        type: 'multiple',
      }}
      columnsState={{
        persistenceKey: 'pro-table-singe-demos',
        persistenceType: 'localStorage',
        defaultValue: {
          option: { fixed: 'right', disable: true },
        },
        onChange(value) {
          console.log('value: ', value);
        },
      }}
      rowKey="id"
      search={{
        labelWidth: 'auto',
      }}
      options={{
        setting: {
          listsHeight: 400,
        },
      }}
      form={{
        // 由于配置了 transform，提交的参与与定义的不同这里需要转化一下
        syncToUrl: (values, type) => {
          if (type === 'get') {
            return {
              ...values,
              created_at: [values.startTime, values.endTime],
            };
          }
          return values;
        },
      }}
      pagination={{
        pageSize: 10,
        onChange: (page) => console.log(page),
      }}
      dateFormatter="string"
      headerTitle="User Center"
      toolBarRender={() => [
        <Button
          key="button"
          icon={<PlusOutlined />}
          onClick={() => {
            actionRef.current?.reload();
          }}
          type="primary"
        >
          新建
        </Button>,
      ]}
    />
  );
};