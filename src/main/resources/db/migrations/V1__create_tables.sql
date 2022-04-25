create table galleries(
    id int not null generated always as identity primary key,
    slug varchar(72) unique not null,
    name varchar(200) unique not null,
    description text not null,
    created_on timestamptz default now(),
    updated_on timestamptz default now()
);

create table images(
    id int not null generated always as identity primary key,
    gallery_id int references galleries(id),
    slug varchar(72) unique not null,
    name varchar(200) unique not null,
    description text not null,
    height smallint not null,
    width smallint not null,
    file_size int not null,
    created_on timestamptz default now(),
    updated_on timestamptz default now()
);

create table resize_workflows(
    id int not null generated always as identity primary key,
    slug varchar(72) unique not null,
    description text,
    created_on timestamptz default now(),
    updated_on timestamptz default now(),
    last_run_on timestamptz
);

create table resize_jobs(
    id int not null generated always as identity primary key,
    image_id int references images(id),
    resize_workflow_id int not null references resize_workflows(id),
    created_on timestamptz default now(),
    updated_on timestamptz default now()
);

create table resize_workflows_steps(
    id int not null generated always as identity primary key,
    slug varchar(72) unique not null,
    name text unique not null,
    ordering smallint not null,
    action text not null,
    resize_workflow_id int references resize_workflows(id),
    created_at timestamptz default now(),
    updated_at timestamptz default now()
);