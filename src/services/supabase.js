import { createClient } from "@supabase/supabase-js";

export const supabaseUrl = "https://hayrypeodyayikntcjxd.supabase.co";
const supabaseKey =
  "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImhheXJ5cGVvZHlheWlrbnRjanhkIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MDU1Nzg4NTYsImV4cCI6MjAyMTE1NDg1Nn0.rRijQAyq7ZdOdYMYaBW8qcNucC6dyDHkhemKikHzUq0";
const supabase = createClient(supabaseUrl, supabaseKey);

export default supabase;
