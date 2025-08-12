import { gql } from '@apollo/client';

export const GENERATE_REPORT = gql`
  mutation GenerateReport($input: ReportRequest!) {
    generateReport(input: $input) {
      reportId
      filename
      reportType
      format
      generatedAt
      fileSizeBytes
      downloadUrl
    }
  }
`;