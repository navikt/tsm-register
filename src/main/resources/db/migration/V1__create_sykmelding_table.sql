create table SYKMELDING
(
    id                          varchar primary key,
    pasient_fnr                 varchar not null,
    pasient_aktoer_id           varchar not null,
    lege_fnr                    varchar not null,
    lege_aktoer_id              varchar not null,
    mottak_id                   varchar not null,
    legekontor_org_nr           varchar,
    legekontor_her_id           varchar,
    legekontor_resh_id          varchar,
    epj_system_navn             varchar not null,
    epj_system_versjon          varchar not null,
    mottatt_tidspunkt           timestamp not null,
    tss_id                      varchar,
    merknader                   jsonb,
    partnerreferanse            varchar,
    lege_hpr                    varchar,
    lege_helsepersonellkategori varchar,
    utenlandsk_sykmelding       jsonb,
    sykmelding                  jsonb,
    validation_result           jsonb
);

create index sykmelding_mottak_id_idx
    on sykmelding (mottak_id);

create index sykmelding_pasient_fnr_idx
    on sykmelding (pasient_fnr);

create index sykmelding_mottatt_tidspunkt_idx
    on sykmelding (mottatt_tidspunkt);


