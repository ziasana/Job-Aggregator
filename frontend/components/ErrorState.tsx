export default function ErrorState() {
  return (
    <div className="mt-8 rounded-lg border border-red-500/30 bg-red-500/5 p-8 text-center text-red-700 dark:text-red-400">
      <p className="font-medium">Couldn&apos;t reach the job search service.</p>
      <p className="mt-1 text-sm">
        Make sure the backend is running (<code>./mvnw spring-boot:run</code>) on{" "}
        <code>http://localhost:8080</code>, then reload this page.
      </p>
    </div>
  );
}
