declare
total int:=0;
sqlStr varchar2(10000);
begin
select count(*) into total  from tab where tname='DATA_EXPORT_TASK' ;
if total<1 then
	sqlStr:='create table data_export_task
(
    id        number primary key,
    start_at  varchar(30),
    end_at    varchar(30),
    failed_at varchar(30),
    details   clob,
    file_name varchar(256),
    file_path varchar(512)
)';
	execute immediate sqlStr;
end if;
end;

###

declare
total int:=0;
sqlStr varchar2(10000);
begin
select count(*) into total  from tab where tname='DB_BASIC_CONFIG' ;
if total<1 then
	sqlStr:='create table  db_basic_config
(
    id    number   primary key ,
    gmt_create   varchar(30),
    gmt_modified varchar(30),
    db_type         varchar(100),
    db_name         varchar(100) unique,
    db_host         varchar(255),
    db_port         int,
    db_user         varchar(255),
    db_password     varchar(255),
    db_url          varchar(255),
    other_params clob
)';
	execute immediate sqlStr;
end if;
end;


###


declare
total int:=0;
sqlStr varchar2(10000);
begin
select count(*) into total  from tab where tname='DB_SECURITY_CONFIG' ;
if total<1 then
	sqlStr:='create table db_security_config
(
    id                          number primary key,
    gmt_create                  varchar(30),
    gmt_modified                varchar(30),
    use_ssl                     int,
    use_ssh_tunnel              int,
    ssl_ca_file                 varchar(200),
    ssl_client_certificate_file varchar(200),
    ssl_client_key_file         varchar(200),
    ssh_proxy_host              varchar(200),
    ssh_proxy_port              int,
    ssh_proxy_user              varchar(200),
    ssh_local_port              int,
    ssh_auth_type               varchar(200),
    ssh_proxy_password          varchar(200),
    ssh_key_file                varchar(512),
    ssh_pass_phrase             varchar(200)
)';
	execute immediate sqlStr;
end if;
end;


###


declare
total int:=0;
sqlStr varchar2(10000);
begin
select count(*) into total  from tab where tname='DS_BASIC_DICTIONARY' ;
if total<1 then
	sqlStr:='create table ds_basic_dictionary
(
    id     number primary key,
    domain_name  varchar(50),
    item_id      varchar(100),
    item_val     varchar(100),
    is_auto      int,
    gmt_create   varchar(30),
    gmt_modified varchar(30)
)';
	execute immediate sqlStr;
end if;
end;


###


declare
total int:=0;
sqlStr varchar2(10000);
begin
select count(*) into total  from tab where tname='DS_DIC_AUTO_CONFIG' ;
if total<1 then
	sqlStr:='create table ds_dic_auto_config
(
    id                  number primary key ,
    gmt_create          varchar(30)  not null,
    gmt_modified        varchar(30)  not null,
    fk_db_id            number       not null,
    schema_name            varchar(100) not null,
    table_name          varchar(100) not null,
    item_id_field_name  varchar(100) not null,
    item_val_field_name varchar(100) not null,
    use_scheduler       int          not null,
    cron                varchar(30)  not null,
    domain_name         varchar(100) not null
)';
	execute immediate sqlStr;
end if;
end;


###



declare
total int:=0;
sqlStr varchar2(10000);
begin
select count(*) into total  from tab where tname='DS_FORM_TABLE_SETTING' ;
if total<1 then
	sqlStr:='
create table ds_form_table_setting
(
    id                                number primary key ,
    gmt_create                        varchar(30),
    gmt_modified                      varchar(30),
    ds_key                            varchar(100),
    db_field_name                     varchar(100),
    db_field_type                     varchar(100),
    view_field_label                  varchar(100),
    db_field_comment                  varchar(500),
    form_field_visible                int,
    form_field_sequence               int,
    form_field_query_type             varchar(100),
    form_field_is_exactly             int,
    form_field_child_field_name       varchar(100),
    form_field_dic_domain_name        varchar(100),
    form_field_use_dic                int,
    form_field_def_val_stratege   varchar(100),
    table_field_visible               int,
    table_field_order_by              varchar(10),
    table_field_query_required        int,
    table_field_sequence              int,
    table_field_column_width          int,
    export_field_visible              int,
    export_field_sequence             int,
    export_field_width                int,
    table_parent_label                varchar(100),
    form_field_use_default_val        int,
    form_field_default_val            varchar(200),
    form_field_default_val_sql        varchar(1024),
    column_is_exist                   int default 1
)';
	execute immediate sqlStr;
end if;
end;


###


declare
total int:=0;
sqlStr varchar2(10000);
begin
select count(*) into total  from tab where tname='DS_KEY_BASIC_CONFIG' ;
if total<1 then
	sqlStr:='create table ds_key_basic_config
(
    id     number  primary key,
    ds_key       varchar(100) unique,
    fk_db_id      number,
    schema_name  varchar(100),
    table_name   varchar(100),
    description  varchar(1024),
    gmt_create   varchar(30),
    gmt_modified varchar(30)
)';
	execute immediate sqlStr;
end if;
end;


###


declare
total int:=0;
sqlStr varchar2(10000);
begin
select count(*) into total  from USER_SEQUENCES where sequence_name='D1_ID_SEQ' ;
if total<1 then
  sqlStr:='create sequence d1_id_seq';
	execute immediate sqlStr;
end if;
end;


###


CREATE or replace VIEW ds_full_config_view as
select t1.id,
       t1.gmt_create,
       t1.gmt_modified,
       t1.db_type,
       t1.db_name,
       t1.db_host,
       t1.db_port,
       t1.db_user,
       t1.db_password,
       t1.db_url,
       t1.other_params,
       t2.gmt_create   as security_gmt_create,
       t2.gmt_modified as security_gmt_modified,
       t2.use_ssl,
       t2.use_ssh_tunnel,
       t2.ssl_ca_file,
       t2.ssl_client_certificate_file,
       t2.ssl_client_key_file,
       t2.ssh_proxy_host,
       t2.ssh_proxy_port,
       t2.ssh_proxy_user,
       t2.ssh_local_port,
       t2.ssh_auth_type,
       t2.ssh_proxy_password,
       t2.ssh_key_file,
       t2.ssh_pass_phrase
from db_basic_config t1
         left outer join db_security_config t2
                         on t1.id = t2.id
