insert into mail_template (ID, TEMPLATE_ID, TITLE, CONTENTS)
values (1, 'expiredate_remain_3_day', '펀딩 상품 마감일 3일 전 안내'
,'<div><p>{USER_NAME}님의 {FUNDING_ID}항목의 펀딩 마감일이 3일 남았습니다.</p></div>'),
(2, 'funding_expired', '펀딩 상품 마감 안내'
,'<div><p>{USER_NAME}님의 {FUNDING_ID}펀딩이 완료되었습니다.</p><p>배송 받을 주소를 확인해주세요.</p></div>');
insert into product(id, price, product_name, ranking)
values(1, 10000, "가죽 장갑", 1),
(2, 1600, "벙어리 장갑", 3),(3, 40000, "고급 장갑", 4);