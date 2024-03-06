import { GithubOutlined } from '@ant-design/icons';
import { DefaultFooter } from '@ant-design/pro-components';
import React from 'react';
import { GITHUB_LINK, SYSTEM_LINK } from '@/constant';

const Footer: React.FC = () => {
  return (
    <DefaultFooter
      style={{
        background: 'none',
      }}
      links={[
        {
          key: 'User Center',
          title: 'User Center',
          href: SYSTEM_LINK,
          blankTarget: true,
        },
        {
          key: 'github',
          title: <GithubOutlined />,
          href: GITHUB_LINK,
          blankTarget: true,
        },
      ]}
    />
  );
};

export default Footer;
