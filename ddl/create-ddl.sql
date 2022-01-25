create database dpp_v2;

create extension "uuid-ossp";

create table if not exists dpp_app_jars
(
    id text not null
        constraint dpp_app_jars_pk
            primary key,
    jar_name text,
    created_at timestamp with time zone,
    updated_at timestamp with time zone
);

create table if not exists dpp_container_info
(
    id text not null
        constraint dpp_container_info_pk
            primary key,
    container_id text,
    container_name text,
    container_url text,
    container_msg text,
    container_version text
);

comment on table dpp_container_info is '容器配置表';

create table if not exists dpp_job_config
(
    id text default uuid_generate_v4() not null
        constraint dpp_job_config_pk
            primary key,
    parallelism integer,
    checkpoint_enable boolean,
    checkpoint_interval integer,
    restart_strategy_count smallint,
    restart_strategy_time integer,
    sql_details text,
    job_id text not null
);

comment on table dpp_job_config is '任务配置表';

comment on column dpp_job_config.parallelism is '并行度';

comment on column dpp_job_config.checkpoint_enable is '检查点开关';

comment on column dpp_job_config.checkpoint_interval is '检查点间隔时间';

comment on column dpp_job_config.restart_strategy_count is '任务异常重启次数';

comment on column dpp_job_config.restart_strategy_time is '任务异常重启间隔时长';

comment on column dpp_job_config.sql_details is 'flinksql详情';

comment on column dpp_job_config.job_id is '任务id';

create unique index if not exists dpp_job_config_job_id_uindex
    on dpp_job_config (job_id);

create table if not exists dpp_job_list
(
    id text default uuid_generate_v4() not null
        constraint dpp_job_list_pk
            primary key,
    job_name text not null,
    job_status text,
    start_time timestamp with time zone,
    run_time bigint,
    container_id text,
    last_check_point_address text,
    describes text,
    enable_schedule boolean,
    job_type text,
    jar_name text,
    app_params text,
    main_class text,
    fv text,
    created_by text,
    created_at timestamp with time zone,
    updated_by text,
    updated_at timestamp with time zone,
    app_id text
);

comment on table dpp_job_list is '任务表';

comment on column dpp_job_list.job_name is '任务名称';

comment on column dpp_job_list.job_status is '任务状态';

comment on column dpp_job_list.start_time is '任务开始时间';

comment on column dpp_job_list.run_time is '任务运行时长';

comment on column dpp_job_list.container_id is '容器id';

comment on column dpp_job_list.last_check_point_address is '最近一次检查点';

comment on column dpp_job_list.describes is '描述';

comment on column dpp_job_list.enable_schedule is '是否开启调度';

comment on column dpp_job_list.job_type is '任务类型';

comment on column dpp_job_list.jar_name is 'jar包名字';

comment on column dpp_job_list.app_params is '运行参数';

comment on column dpp_job_list.main_class is '主类';

comment on column dpp_job_list.fv is 'flink版本号';

comment on column dpp_job_list.app_id is '集群任务id';

create unique index if not exists dpp_job_list_job_name_uindex
    on dpp_job_list (job_name);

create table if not exists dpp_job_log
(
    job_id text not null
        constraint dpp_job_log_pk
            primary key,
    log_info text
);

comment on table dpp_job_log is '任务日志';

create unique index if not exists dpp_job_log_job_id_uindex
    on dpp_job_log (job_id);

create table if not exists dpp_job_scheduled
(
    id text not null
        constraint dpp_job_scheduled_pk
            primary key,
    job_name text,
    cron text,
    created_by text,
    created_at timestamp with time zone,
    updated_by text,
    updated_at timestamp with time zone,
    is_open boolean,
    job_type text
);

create table if not exists dpp_user
(
    id text not null
        constraint dpp_user_pk
            primary key,
    user_name text,
    password text,
    created_at timestamp with time zone default now(),
    updated_at timestamp with time zone default now()
);

comment on table dpp_user is '用户表';

insert into dpp_user (id, user_name, password, created_at, updated_at)
values ('c7cb6921-b51f-4759-80cf-c1d766e8d01d','admin','123456','2021-06-21 08:44:33','2021-06-21 08:44:33'
);

alter table dpp_container_info add column container_type int;
comment on column dpp_container_info.container_type is  '容器类型(1：共享，2：独享)';
alter table dpp_container_info add column jm int;
comment on column dpp_container_info.jm is  'jobmanager配置(MB)';
alter table dpp_container_info add column tm int;
comment on column dpp_container_info.tm is  'taskmanager配置(MB)';

alter table dpp_job_config add column jm int;
comment on column dpp_job_config.jm is  'jobmanager配置(MB)';
alter table dpp_job_config add column tm int;
comment on column dpp_job_config.tm is  'taskmanager配置(MB)';

alter table dpp_job_config add column container_type int;
comment on column dpp_job_config.container_type is  '容器类型(1：共享，2：独享)';

alter table dpp_job_config add column ys int;
comment on column dpp_job_config.ys is  'tm solt数';

INSERT INTO public.dpp_container_info
(id, container_id, container_name, container_url, container_msg, container_version, container_type, jm, tm)
VALUES('b778bea0-9fb8-43ac-9bd1-0d75adc19e08', 'built-in', 'built-in', 'built-in', 'built-in', 'flink-1.13.1', 2, NULL, NULL);

INSERT INTO public.dpp_container_info
(id, container_id, container_name, container_url, container_msg, container_version, container_type, jm, tm)
VALUES('a89b49da-79d4-43b6-8237-663dc763d473', 'built-in', 'built-in', 'built-in', 'built-in', 'flink-1.10.0', 2, NULL, NULL);

alter table dpp_job_config add column warning_enable  bool;
comment on column dpp_job_config.warning_enable is  '是否开启报警';

alter table dpp_job_config add column warning_config jsonb;
comment on column dpp_job_config.warning_config is  '报警配置';

alter table dpp_container_info add column c_type text;
comment on column dpp_container_info.c_type is  '容器类型(yarn，docker，k8s)';

ALTER TABLE dpp_app_jars ADD c_type text NOT NULL;
comment on column dpp_app_jars.c_type is  '容器类型(yarn，docker，k8s)';
