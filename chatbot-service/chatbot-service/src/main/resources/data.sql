INSERT INTO placeholder (id, name, icon) VALUES
(1, 'Ask about medical insurance', 'U+1F3A5'),
(2, 'Ask about HR related queries', 'U+2764 U+FE0F'),
(3, 'Ask about policy related queries', 'U+1F697'),
ON CONFLICT (id) DO NOTHING;

INSERT INTO option (id, name, icon) VALUES
(1, 'HR Related', '👩🏻‍💼'),
(2, 'Policy Related', '📑'),
(3, 'Medical Insurance', '🏥'),
(4, 'Policy Tracker', '🛡️'),
(5, 'Sales Related', '💼'),
(6, 'Product Brochure', '📒'),
(7, 'Premium Payment', 'U+1F4B3'),
(8, 'Buy New Policy', 'U+1F6D2'),
(9, 'Download Documents', 'U+1F4E5'),
(10, 'Update Profile', 'U+1F464')
ON CONFLICT (id) DO NOTHING;

