create table galleries(
    id int not null generated always as identity primary key,
    slug varchar(72) unique not null,
    name varchar(200) unique not null,
    description text not null,
    created_on timestamptz default now(),
    updated_on timestamptz default now()
);

create table original_images(
    id int not null generated always as identity primary key,
    gallery_id int references galleries(id),
    filename_hash varchar(4) unique not null, -- path without file separator
    path varchar(6) unique not null,
    name varchar(200) unique not null,
    description text not null,
    height smallint not null,
    width smallint not null,
    file_size int not null,
    created_on timestamptz default now(),
    updated_on timestamptz default now()
);

create table scaled_images(
    id int not null generated always as identity primary key,
    original_image_id int not null references original_images(id),
    path varchar(6) unique not null,
    marked_for_cleanup boolean not null,
    created_on timestamptz default now(),
    updated_on timestamptz default now()
);

create table resize_pipelines(
    id int not null generated always as identity primary key,
    hash uuid unique not null,
    description text,
    last_run_on timestamptz,
    created_on timestamptz default now(),
    updated_on timestamptz default now()
);

create table resize_jobs_statuses(
    id int not null generated always as identity primary key,
    status varchar(20) not null unique,
    created_on timestamptz default now(),
    updated_on timestamptz default now()
);

create table resize_jobs(
    id int not null generated always as identity primary key,
    image_id int references original_images(id),
    hash uuid unique not null,
    status_id int not null references resize_jobs_statuses(id),
    resize_pipeline_id int not null references resize_pipelines(id),
    created_on timestamptz default now(),
    updated_on timestamptz default now()
);

create table resize_pipelines_steps(
    id int not null generated always as identity primary key,
    hash uuid unique not null,
    name text unique not null,
    ordering smallint not null,
    action text not null,
    resize_pipeline_id int references resize_pipelines(id),
    created_at timestamptz default now(),
    updated_at timestamptz default now()
);