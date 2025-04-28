
---

## 🚀 Deployment Steps

### 1. Setup IAM Role
- Create a role `MediaConvertServiceRole` with permissions for:
  - MediaConvert full access
  - S3 read/write access
- Attach to the Lambda function.

### 2. Setup Lambda Function
- Runtime: **Python 3.10**
- Handler: `lambda_function.lambda_handler`
- Trigger: S3 event (Object Created) from bucket `vodsie/videos/`

### 3. Configure MediaConvert
- No special setup; the Lambda handles endpoint discovery automatically.

### 4. CloudFront Setup
- Set up a CloudFront distribution pointing to your S3 bucket for fast HLS delivery.

---

## 📄 Important Environment Variables

| Variable | Description |
|:---|:---|
| `MEDIA_CONVERT_ROLE` | ARN of the MediaConvert IAM Role |
| `S3_BUCKET` | Source/Target bucket name (`vodsie`) |
| `OUTPUT_PREFIX` | Output path inside the bucket (`hls/converted/`) |
| `REGION` | AWS Region (`eu-west-1`) |

---

## 📦 Example MediaConvert Job Submission

- Input: `s3://vodsie/videos/uploaded_video.mp4`
- Output: `s3://vodsie/hls/converted/full_video.m3u8`
- Public URL: `https://your-cloudfront-domain.net/hls/converted/full_video.m3u8`

---

## 👨‍💻 Author

- **Sammy Ocharo Obanyi**  
- [LinkedIn Profile](www.linkedin.com/in/sammy-ocharo-82943a1a1)  
- AWS Cloud Engineer | Microservices | Software Developer 

---

## 📜 License

This project is licensed under the [MIT License](LICENSE).

---

