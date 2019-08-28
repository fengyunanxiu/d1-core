create table if not exists data_export_task
(
    id        serial primary key,
    start_at  varchar(30),
    end_at    varchar(30),
    failed_at varchar(30),
    details   text,
    file_name varchar(256),
    file_path varchar(512)
);

###

create table if not exists db_basic_config
(
    id           serial primary key,
    gmt_create   varchar(30),
    gmt_modified varchar(30),
    db_type         varchar(100),
    db_name         varchar(100) unique not null ,
    db_host         varchar(255),
    db_port         int,
    db_user         varchar(255),
    db_password     varchar(255),
    db_url          varchar(255),
    other_params text
);

###

create table if not exists db_security_config
(
    id                          bigint primary key,
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
    ssh_key_content             text,
    ssh_pass_phrase             varchar(200)
);

###

create table if not exists df_form_table_setting
(
    id                                serial primary key,
    gmt_create                        varchar(30),
    gmt_modified                      varchar(30),
    df_key                            varchar(100),
    db_field_name                     varchar(100),
    db_field_type                     varchar(100),
    view_field_label                  varchar(100),
    db_field_comment                  varchar(500),
    form_field_visible                int,
    form_field_sequence               int,
    form_field_query_type             varchar(100),
    form_field_child_field_name       varchar(100),
    form_field_dict_domain_name       varchar(100),
    form_field_dict_item              varchar(100),
    form_field_def_val_strategy       varchar(100),
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
    column_is_exist                   int default 1
);
###
create index index_df_key
    on df_form_table_setting (df_key);

###

create table if not exists df_key_basic_config
(
    id           serial primary key,
    df_key       varchar(100) unique,
    fk_db_id     bigint,
    schema_name  varchar(100),
    table_name   varchar(100),
    description  varchar(1024),
    gmt_create   varchar(30),
    gmt_modified varchar(30)
);


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
       t2.ssh_key_content,
       t2.ssh_pass_phrase
from db_basic_config t1
         left outer join db_security_config t2
                         on t1.id = t2.id;


###

create table if not exists db_dict (
    field_id varchar(64) primary key ,
    field_gmt_create timestamp,
    field_gmt_modified timestamp,
    field_domain varchar(64) not null ,
    field_item varchar(64) not null ,
    field_value varchar(100) not null ,
    field_label varchar(100),
    field_sequence int,
    field_parent_id varchar(64),
    domain_item_gmt_create timestamp,
) ;
###
create index  db_dict_unique_idx
	on db_dict(field_domain, field_item, field_value);

###

create table if not exists db_form_dict_configuration(
    field_id varchar(64) primary key,
    field_form_df_key varchar(100) not null,
    field_form_field_key varchar(100) not null,
    field_domain varchar(64),
    field_item varchar(64)
) ;

###

create table if not exists db_defaults_configuration(
    field_id varchar(64) primary key,
    field_form_df_key varchar(100) not null,
    field_form_field_key varchar(100) not null,
    field_type varchar(64) ,
    field_plugin_conf text ,
    field_manual_conf text
);

###

create table if not exists db_dict_plugin_configuration(
    field_id varchar(64) primary key ,
    field_domain varchar(100) not null,
    field_item varchar(100) not null,
    field_enable boolean not null,
    field_type varchar(50) not null,
    field_param text,
    field_cron varchar(100)
);

###

create table if not exists ds_tree_menu_cache
(
    ds_id              bigint  not null primary key,
    ds_basic_info      json   null,
    ds_schema_info     json   null,
    ds_table_view_info json   null,
    ds_key_info        json   null
);

