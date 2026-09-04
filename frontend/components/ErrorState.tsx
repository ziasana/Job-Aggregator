export default function ErrorState() {
  return (
    <div className="rounded-2xl border border-red-200 bg-red-50 p-12 text-center text-red-700">
      <p className="font-bold">Couldn&apos;t reach the job search service.</p>
      <p className="mt-1 text-sm">
        Make sure the backend is running (<code>./mvnw spring-boot:run</code>) on{" "}
        <code>http://localhost:8080</code>, then reload this page.
      </p>
    </div>
  );
}
