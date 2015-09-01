insert into email_templates (id, mnemo, subject, html)
values (nextval('seq_email_templates'), 'registration', 'Регистрация на сайте autopart-nk.ru', 'Вы зарегистрировались на сайте autopart-nk.ru. Код активации Вашего аккаунта: {{user.code}}.');

insert into email_templates (id, mnemo, subject, html)
values (nextval('seq_email_templates'), 'activation', 'Активация аккаунта на сайте autopart-nk.ru', 'Вы успешно активировали аккаунт на сайте autopart-nk.ru.');

insert into email_templates (id, mnemo, subject, html)
values (nextval('seq_email_templates'), 'order', 'Новый заказ с сайта autopart-nk.ru', 'Поступил заказ с сайта autopart-nk.ru. Ссылка на новый заказ: http://new.autopart-nk.ru/admin/order/{{order.id}}');

insert into email_templates (id, mnemo, subject, html)
values (nextval('seq_email_templates'), 'request', 'Новый запрос с сайта autopart-nk.ru', 'Поступил запрос с сайта autopart-nk.ru. Ссылка на новый запрос: http://new.autopart-nk.ru/admin/request/{{request.id}}');

insert into settings (id, key, val)
values (nextval('seq_settings'), 'catelog.new-parts-category', '78e6afb0-dd36-11e4-964c-240a64d5f8a8');

insert into settings (id, key, val)
values (nextval('seq_settings'), 'catalog.default-price', '78e6afb7-dd36-11e4-964c-240a64d5f8a8');

insert into settings (id, key, val)
values (nextval('seq_settings'), 'view.per-page', '50');

insert into settings (id, key, val)
values (nextval('seq_settings'), 'path.temp-subdirectory', 'autopart');

insert into settings (id, key, val)
values (nextval('seq_settings'), 'email.from.login', 'noreply@autopart-nk.ru');

insert into settings (id, key, val)
values (nextval('seq_settings'), 'email.from.password', '39q8gjf9gubuqg5h');

insert into settings (id, key, val)
values (nextval('seq_settings'), 'email.to', 'admin@autopart-nk.ru');

insert into settings (id, key, val)
values (nextval('seq_settings'), 'catalog.category-default-image', 'http://images.greenworm.ru/images/H39FLSHV0M/ZADOSMYBFW.jpg');

insert into settings (id, key, val)
values (nextval('seq_settings'), 'search.min-length', '3');

insert into settings (id, key, val)
values (nextval('seq_settings'), 'images.upload-url', 'http://images.greenworm.ru');

insert into settings (id, key, val)
values (nextval('seq_settings'), 'images.server', '/gate/add?fmt=json&process=0&service_id=2000');

insert into settings (id, key, val)
values (nextval('seq_settings'), 'catalog.skip-categories', '9e742a16-e346-11e4-a36b-240a64d5f8a8,bd33c51c-e8f0-11e4-be23-240a64d5f8a8');

insert into roles (id, name, position)
values (nextval('seq_roles'), 'USER', 1);

insert into roles (id, name, position)
values (nextval('seq_roles'), 'ADMIN', 2);

insert into users (id, email, password_hash, status, type)
values (nextval('seq_users'), 'admin@autopart-nk.ru', '$2a$10$zqKvmElfaSKSrOZfXf1KKOOLyMRZ40uv4WRSucB06s4283B/Urj16', 1, 0);

insert into users_roles (user_id, role_id)
values ((select id from users where email = 'admin@autopart-nk.ru'), (select id from roles where name = 'USER'));

insert into users_roles (user_id, role_id)
values ((select id from users where email = 'admin@autopart-nk.ru'), (select id from roles where name = 'ADMIN'));