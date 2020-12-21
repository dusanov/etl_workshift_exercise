drop table if exists shift_allowances CASCADE;
drop table if exists shift_award_interpretations CASCADE;
drop table if exists shift_breaks CASCADE;
drop table if exists shifts CASCADE;
drop table if exists batches CASCADE;
drop table if exists batches_shifts_failed CASCADE;

drop sequence if exists hibernate_sequence;
create sequence hibernate_sequence start with 1 increment by 1;

   create table batches (
       id integer not null,
        date_created timestamp,
        primary key (id)
    );

    create table batches_shifts_failed (
       id integer not null,
        batch_id integer,
        dto varchar(255),
        error_message varchar(255),
        shift_id integer,
        primary key (id)
    );

    create table shifts (
       id integer not null,
	   batch_id integer,
        allowance_cost decimal,
        approved_at timestamp,
        approved_by integer,
        award_cost decimal,
        cost decimal,
        date varchar(255),
        department_id integer,
        finish timestamp,
        last_costed_at timestamp,
        leave_request_id integer,
        metadata varchar(255),
        record_id integer,
        shift_feedback_id integer,
        start timestamp,
        status varchar(255),
        sub_cost_centre varchar(255),
        tag varchar(255),
        tag_id integer,
        timesheet_id integer,
        updated_at timestamp,
        user_id integer,
        primary key (id)
    );
	
    create table shift_breaks (
       id integer not null,
        finish timestamp,
        length integer,
        paid boolean,
        shift_date varchar(255),
        shift_id integer,
        start timestamp,
        sheet_id integer,
        updated_at timestamp,
        primary key (id)
    );
	
    create table shift_award_interpretations (
       id integer not null,
        cost decimal,
        date varchar(255),
        export_name varchar(255),
        "from" timestamp,
        ordinary_hours boolean,
        secondary_export_name varchar(255),
        shift_date varchar(255),
        shift_id integer,
        sheet_id integer,
        "to" timestamp,
        units decimal,
        primary key (id)
    );
	
    create table shift_allowances (
       id integer not null,
        cost decimal,
        name varchar(255),
        shift_date varchar(255),
        shift_id integer,
        sheet_id integer,
        updated_at timestamp,
        value decimal,
        primary key (id)
    );