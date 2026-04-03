-- Sample catalog and members; loan ties Design Patterns to Alex (no available copy left)
-- ISBNs stored without hyphens so they match create() normalization logic
INSERT INTO books (title, author, isbn, total_copies, available_copies) VALUES
  ('Clean Code', 'Robert C. Martin', '9780132350884', 2, 2),
  ('Effective Java', 'Joshua Bloch', '9780134685991', 1, 1),
  ('Design Patterns', 'Gang of Four', '9780201633612', 1, 0);

INSERT INTO members (name, email) VALUES
  ('Alex Rivera', 'alex.rivera@example.com'),
  ('Jordan Lee', 'jordan.lee@example.com');

INSERT INTO loans (book_id, member_id, loan_date, due_date, returned_at)
SELECT b.id, m.id, DATE '2026-03-01', DATE '2026-03-15', NULL
FROM books b, members m
WHERE b.isbn = '9780201633612' AND m.email = 'alex.rivera@example.com';
