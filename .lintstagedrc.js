module.exports = {
  // Delegate the whole formatting to spotless
  "*": [
    (resolvedPaths) => {
      return `./mvnw -Pcheckstyle spotless:apply -X -DspotlessFiles=${resolvedPaths
        .map((path) => `'${path}'`)
        .join(",")}`;
    },
  ],
};
