// @ts-ignore
/* eslint-disable */

declare namespace API {
  type CurrentUser = {
      id?: number;
      userAccount?: string;
      avatarUrl?: string;
      phone?: string;
      email?: string;
      username?: string;
      gender?: number;
      userRole?: number;

      // name?: string;
      // avatar?: string;
      // userid?: string;
      // // email?: string;
      // signature?: string;
      // title?: string;
      // group?: string;
      // tags?: { key?: string; label?: string }[];
      // notifyCount?: number;
      // unreadCount?: number;
      // country?: string;
      // access?: string;
      // geographic?: {
      //   province?: { label?: string; key?: string };
      //   city?: { label?: string; key?: string };
      // };
      // address?: string;
      // phone?: string;
  };

  type BaseResponse<T> = {
    code: number;
    data: T;
    message: string;
    description: string;
  };

  type LoginResult = {
    id?: number;
    status?: string;
    type?: string;
    currentAuthority?: string;
  };

  type RegisterResult = number;

  type SearchResult = {
    data?: CurrentUser[];
    /** 列表的内容总数 */
    total?: number;
    success?: boolean;
  };

  type ModifyResult = boolean;

  type PageParams = {
    current?: number;
    pageSize?: number;
  };

  type RuleListItem = {
    key?: number;
    disabled?: boolean;
    href?: string;
    avatar?: string;
    name?: string;
    owner?: string;
    desc?: string;
    callNo?: number;
    status?: number;
    updatedAt?: string;
    createdAt?: string;
    progress?: number;
  };

  type RuleList = {
    data?: RuleListItem[];
    /** 列表的内容总数 */
    total?: number;
    success?: boolean;
  };

  type FakeCaptcha = {
    code?: number;
    status?: string;
  };

  type LoginParams = {
    userAccount?: string;
    userPassword?: string;
    autoLogin?: boolean;
    type?: string;
  };

  type RegisterParams = {
    userAccount?: string;
    userPassword?: string;
    checkPassword?: string;
  };

  type SearchParams = {
    userAccount?: string;
  };

  type ModifyParams = {
    userAccount?: string;
    userName?: string;
    phone?: string;
    email?: string;
    avatarUrl?: string;
    gender?: number;
  };

  type ErrorResponse = {
    /** 业务约定的错误码 */
    errorCode: string;
    /** 业务上的错误信息 */
    errorMessage?: string;
    /** 业务上的请求是否成功 */
    success?: boolean;
  };

  type NoticeIconList = {
    data?: NoticeIconItem[];
    /** 列表的内容总数 */
    total?: number;
    success?: boolean;
  };

  type NoticeIconItemType = 'notification' | 'message' | 'event';

  type NoticeIconItem = {
    id?: string;
    extra?: string;
    key?: string;
    read?: boolean;
    avatar?: string;
    title?: string;
    status?: string;
    datetime?: string;
    description?: string;
    type?: NoticeIconItemType;
  };
}


