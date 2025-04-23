INSERT INTO placeholder (id, name, icon) VALUES
(1, 'Ask about medical insurance', '\ud83c\udfa5'),
(2, 'Ask about HR related queries', '\u2764\ufe0f'),
(3, 'Ask about policy related queries', '\ud83d\ude97')
ON CONFLICT (id) DO NOTHING;

INSERT INTO option (id, name, icon) VALUES
(1, 'HR Related', '\ud83d\udc69\u200d\ud83d\udcbc'),
(2, 'Policy Related', '\ud83d\udcd1'),
(3, 'Medical Insurance', '\ud83c\udfe5'),
(4, 'Policy Tracker', '\ud83d\udee1'),
(5, 'Sales Related', '\ud83d\udcbc'),
(6, 'Product Brochure', '\ud83d\udcd2'),
(7, 'Premium Payment', '\ud83d\udcb3'),
(8, 'Buy New Policy', '\ud83d\uded2'),
(9, 'Download Documents', '\ud83d\udce5'),
(10, 'Update Profile', '\ud83d\udc64')
ON CONFLICT (id) DO NOTHING;



