import { HeartTwoTone, SmileTwoTone } from '@ant-design/icons';
import { PageContainer } from '@ant-design/pro-components';
import '@umijs/max';
import { Alert, Card, Typography } from 'antd';
import React from 'react';
const Admin: React.FC = () => {
  return (
    <PageContainer content={' 这个页面只有 admin 权限才能查看'}>
      <p
        style={{
          textAlign: 'center',
          marginTop: 24,
        }}
      >
      </p>
    </PageContainer>
  );
};
export default Admin;
