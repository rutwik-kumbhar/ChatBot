CREATE TABLE IF NOT EXISTS placeholder (
    id INT PRIMARY KEY,
    name VARCHAR(255),
    icon VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS option (
    id INT PRIMARY KEY,
    name VARCHAR(255),
    icon VARCHAR(20)
);


INSERT INTO placeholder (id, name, icon) VALUES
(1, 'Asked your Health Insurance queries', 'U+1F3A5'),
(2, 'Asked your Life Insurance queries', 'U+2764 U+FE0F'),
(3, 'Asked your Car Insurance queries', 'U+1F697'),
(4, 'Asked your Policy Renewal queries', 'U+1F504'),
(5, 'Asked your Claim Status queries', 'U+1F4C4'),
(6, 'Asked your Customer Support queries', 'U+1F4DE'),
(7, 'Asked your Premium Payment queries', 'U+1F4B3'),
(8, 'Asked your Buy New Policy queries', 'U+1F6D2'),
(9, 'Asked your Download Documents queries', 'U+1F4E5'),
(10, 'Asked your Update Profile queries', 'U+1F464')
ON CONFLICT (id) DO NOTHING;

INSERT INTO option (id, name, icon) VALUES
(1, 'Health Insurance', 'U+1F3A5'),
(2, 'Life Insurance', 'U+2764 U+FE0F'),
(3, 'Car Insurance', 'U+1F697'),
(4, 'Policy Renewal', 'U+1F504'),
(5, 'Claim Status', 'U+1F4C4'),
(6, 'Customer Support', 'U+1F4DE'),
(7, 'Premium Payment', 'U+1F4B3'),
(8, 'Buy New Policy', 'U+1F6D2'),
(9, 'Download Documents', 'U+1F4E5'),
(10, 'Update Profile', 'U+1F464')
ON CONFLICT (id) DO NOTHING;