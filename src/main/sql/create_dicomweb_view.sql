drop view dicomweb_series_view;
drop view dicomweb_study_view;

create view dicomweb_study_view AS
select
  sbj.id as sbj_id,
  sbj.label as sbj_label,
  prj.id as prj_id,
  prj.name as prj_name,
  sess_data.id as session_id,
  expt_data.date as studyDate,
  expt_data.time as studyTime,
  sess_data.dcmaccessionnumber as accessionNumber,
  sess_data.modality as modalities,
  sess_data.dcmpatientname as patientName,
  sess_data.dcmpatientid as patientId,
  sess_data.uid as studyInstanceUID,
  demographics.gender
from xnat_subjectdata as sbj
  inner join xnat_projectdata prj on (sbj.project = prj.id)
  inner join xnat_experimentdata as expt_data on expt_data.project = prj.id
  inner join xnat_subjectassessordata as sbj_acc_data on (sbj_acc_data.id = expt_data.id and sbj_acc_data.subject_id = sbj.id)
  inner join xnat_imagesessiondata as sess_data on sess_data.id = sbj_acc_data.id
  left join xnat_demographicdata as demographics
  on sbj.demographics_xnat_abstractdemographicdata_id = demographics.xnat_abstractdemographicdata_id;

create view dicomweb_series_view AS
SELECT
  study_view.*,
  scan.modality as modality,
  scan.uid as seriesInstanceUID,
  scan.id as seriesNumber
  from dicomweb_study_view as study_view,
    xnat_imagescandata as scan
  where scan.image_session_id = study_view.session_id;

select * from dicomweb_study_view order by sbj_id;

select * from dicomweb_series_view order by sbj_id;

select * from dicomweb_study_view where patientId = 'FacilityA1-06';
select * from dicomweb_study_view where patientId like 'Fac%';
select * from dicomweb_study_view where patientId like '%ac%';

select * from dicomweb_study_view where accessionNumber like 'Alph%';

-- 201708010  is everything today
-- 20170810-20170831 is everything between the 10th and 31st
-- 20170810- is everything on the 10th and after
-- -20170810 is everything before the 10th

-- everthing on 20170506
select * from dicomweb_study_view where studyDate = '2017-05-06';
select * from dicomweb_study_view where studyDate = '20170506';

-- 20170505-20170507 everything between. (implemented as not inclusive of either boundary.)
select * from dicomweb_study_view where studyDate > '20170505' and studyDate < '20170507'  ;

-- 20170506- is everything on and after
select * from dicomweb_study_view where studyDate >= '20170506';

-- -20170506 is everything before
select * from dicomweb_study_view where studyDate < '20170506';

