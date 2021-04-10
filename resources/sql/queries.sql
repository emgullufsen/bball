-- :name insert-baller! :! :n
-- :doc inserts baller into table
insert into ballers
(name)
values (:name)

-- :name get-ballers :? :*
-- :doc gets balers
select * from ballers
